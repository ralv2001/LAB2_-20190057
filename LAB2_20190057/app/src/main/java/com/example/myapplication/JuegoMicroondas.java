package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JuegoMicroondas extends AppCompatActivity {

    private HashMap<Integer, String> preguntas;
    private HashMap<Integer, String> respuestas;
    private ArrayList<Integer> indices;
    private ArrayList<ArrayList<String>> listaCompletaOpciones;
    private ArrayList<Integer> respuestasUsuario;
    private AtomicInteger currentIndexQuestion;
    private AtomicInteger optionSelected;
    private long tiempoInicio;
    private boolean juegoCompletado = false;
    private boolean estadisticaGuardada = false;
    private boolean mostrandoResultado = false;

    // Variables para guardar el estado del juego
    private static final String ESTADO_JUEGO = "estado_juego_microondas";
    private static boolean hayEstadoGuardado = false;
    private static int indexPreguntaGuardada = 0;
    private static ArrayList<Integer> respuestasGuardadas = new ArrayList<>();
    private static int puntajeGuardado = 0;
    private static long tiempoInicioGuardado = 0;
    private static boolean juegoCompletadoGuardado = false;
    private static boolean estadisticaGuardadaFlag = false;
    private static boolean mostrandoResultadoGuardado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_juego_microondas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar el botón para ir a la pantalla principal (cancelar juego)
        ImageButton goBack = findViewById(R.id.btnToMain);
        goBack.setOnClickListener(view -> {
            // Guardar estadística de juego cancelado solo si no se ha completado y no es un juego ya terminado
            if (!juegoCompletado && !estadisticaGuardada) {
                SharedPreferences prefs = getSharedPreferences("TeleQuizStats", MODE_PRIVATE);
                Estadisticas.guardarEstadistica(prefs, "Microondas", false, 0, 0);
                estadisticaGuardada = true;
            }

            // Limpiar estado guardado
            hayEstadoGuardado = false;

            // Volver a la pantalla principal
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish(); // Cerrar esta actividad
        });

        // Configurar botón para ver estadísticas
        ImageButton btnStats = findViewById(R.id.btnToStatistics);
        btnStats.setOnClickListener(view -> {
            // Guardar estado actual del juego
            guardarEstadoJuego();

            Intent intent = new Intent(getApplicationContext(), Estadisticas.class);
            intent.putExtra("origin", this.getClass().getName());
            startActivity(intent);
        });

        // INICIALIZACIÓN DE DATOS
        preguntas = new HashMap<>();
        preguntas.put(0, "¿En qué rango de frecuencias suelen operar las redes Wi-Fi?");
        preguntas.put(1, "¿Qué problema es común en enlaces de microondas?");
        preguntas.put(2, "¿Qué es la zona de Fresnel en microondas?");
        preguntas.put(3, "¿Qué ventaja tienen las comunicaciones por microondas?");
        preguntas.put(4, "¿Qué dispositivo se usa para enfocar señales de microondas?");

        // Lista de respuestas correctas
        respuestas = new HashMap<>();
        respuestas.put(0, "2.4 GHz y 5 GHz");
        respuestas.put(1, "Interferencia");
        respuestas.put(2, "Área de propagación");
        respuestas.put(3, "Alta velocidad");
        respuestas.put(4, "Antena parabólica");

        // Comprobar si venimos de estadísticas y tenemos un estado guardado
        boolean fromStats = getIntent().getBooleanExtra("fromStats", false);

        if (fromStats && hayEstadoGuardado) {
            // Restaurar estado del juego guardado
            restaurarEstadoJuego();
        } else {
            // Iniciar nuevo juego
            iniciarNuevoJuego();
        }

        Button btnOpcion1 = findViewById(R.id.btnOpcion1Microondas);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Microondas);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Microondas);
        Button btnSiguiente = findViewById(R.id.btnSiguienteMicroondas);
        Button btnAnterior = findViewById(R.id.btnAnteriorMicroondas);

        // Configurar listeners para botones de opciones
        btnOpcion1.setOnClickListener(view -> {
            // Corroboramos respuesta
            String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));
            boolean isRespuestaCorrecta = btnOpcion1.getText().toString().equals(respuestaCorrecta);
            setBgColorBtn(btnOpcion1, isRespuestaCorrecta);
            updatePuntaje(isRespuestaCorrecta);
            // Bloqueamos los otros botones
            btnOpcion2.setEnabled(false);
            btnOpcion3.setEnabled(false);
            // Habilitamos el botón siguiente
            btnSiguiente.setEnabled(true);
            optionSelected.set(0);
            respuestasUsuario.add(optionSelected.get());
        });

        btnOpcion2.setOnClickListener(view -> {
            String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));
            boolean isRespuestaCorrecta = btnOpcion2.getText().toString().equals(respuestaCorrecta);
            setBgColorBtn(btnOpcion2, isRespuestaCorrecta);
            updatePuntaje(isRespuestaCorrecta);
            btnOpcion1.setEnabled(false);
            btnOpcion3.setEnabled(false);
            btnSiguiente.setEnabled(true);
            optionSelected.set(1);
            respuestasUsuario.add(optionSelected.get());
        });

        btnOpcion3.setOnClickListener(view -> {
            String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));
            boolean isRespuestaCorrecta = btnOpcion3.getText().toString().equals(respuestaCorrecta);
            setBgColorBtn(btnOpcion3, isRespuestaCorrecta);
            updatePuntaje(isRespuestaCorrecta);
            btnOpcion1.setEnabled(false);
            btnOpcion2.setEnabled(false);
            btnSiguiente.setEnabled(true);
            optionSelected.set(2);
            respuestasUsuario.add(optionSelected.get());
        });

        btnSiguiente.setOnClickListener(view -> {
            // Si es la última pregunta y ya tiene respuesta, mostrar resultados
            if (currentIndexQuestion.get() == 4 && respuestasUsuario.size() > 4) {
                mostrarResultadoFinal();
            }
            // Si no es la última pregunta, verificamos si es la última respondida
            else if (currentIndexQuestion.get() < 4) {
                // Avanzar a la siguiente pregunta
                currentIndexQuestion.set(currentIndexQuestion.get() + 1);

                // Actualizar la interfaz
                updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()),
                        indices, currentIndexQuestion.get(), respuestasUsuario.size());

                // Si esta pregunta ya fue respondida, mostrar la respuesta seleccionada
                if (currentIndexQuestion.get() < respuestasUsuario.size()) {
                    btnOpcion1.setEnabled(false);
                    btnOpcion2.setEnabled(false);
                    btnOpcion3.setEnabled(false);

                    int respuestaSeleccionada = respuestasUsuario.get(currentIndexQuestion.get());
                    String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));

                    switch (respuestaSeleccionada) {
                        case 0:
                            setBgColorBtn(btnOpcion1, btnOpcion1.getText().toString().equals(respuestaCorrecta));
                            break;
                        case 1:
                            setBgColorBtn(btnOpcion2, btnOpcion2.getText().toString().equals(respuestaCorrecta));
                            break;
                        case 2:
                            setBgColorBtn(btnOpcion3, btnOpcion3.getText().toString().equals(respuestaCorrecta));
                            break;
                    }
                }

                // Si llegamos a la última pregunta y ya está respondida, habilitar botón para mostrar resultados
                if (currentIndexQuestion.get() == 4 && respuestasUsuario.size() > 4) {
                    btnSiguiente.setEnabled(true);
                }

                // Siempre habilitar el botón Anterior si no estamos en la primera pregunta
                btnAnterior.setEnabled(currentIndexQuestion.get() > 0);
            }
        });

        btnAnterior.setOnClickListener(view -> {
            // Si estamos mostrando el resultado final, volver a la última pregunta
            LinearLayout layoutResultado = findViewById(R.id.layoutResultadoMicroondas);
            if (layoutResultado.getVisibility() == View.VISIBLE) {
                layoutResultado.setVisibility(View.GONE);
                mostrandoResultado = false;

                TextView textoPregunta = findViewById(R.id.textoPreguntaMicroondas);
                textoPregunta.setVisibility(View.VISIBLE);

                btnOpcion1.setVisibility(View.VISIBLE);
                btnOpcion2.setVisibility(View.VISIBLE);
                btnOpcion3.setVisibility(View.VISIBLE);

                // Restaurar la última pregunta y su respuesta
                currentIndexQuestion.set(4);
                updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()),
                        indices, currentIndexQuestion.get(), respuestasUsuario.size());

                // Mostrar la respuesta seleccionada
                btnOpcion1.setEnabled(false);
                btnOpcion2.setEnabled(false);
                btnOpcion3.setEnabled(false);

                int respuestaSeleccionada = respuestasUsuario.get(currentIndexQuestion.get());
                String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));

                switch (respuestaSeleccionada) {
                    case 0:
                        setBgColorBtn(btnOpcion1, btnOpcion1.getText().toString().equals(respuestaCorrecta));
                        break;
                    case 1:
                        setBgColorBtn(btnOpcion2, btnOpcion2.getText().toString().equals(respuestaCorrecta));
                        break;
                    case 2:
                        setBgColorBtn(btnOpcion3, btnOpcion3.getText().toString().equals(respuestaCorrecta));
                        break;
                }

                // Habilitar el botón siguiente para volver a resultados
                btnSiguiente.setText("Siguiente");
                btnSiguiente.setEnabled(true);
            }
            // Si no estamos en resultados, navegar a la pregunta anterior
            else {
                currentIndexQuestion.set(currentIndexQuestion.get() - 1);
                updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()),
                        indices, currentIndexQuestion.get(), respuestasUsuario.size());

                btnOpcion1.setEnabled(false);
                btnOpcion2.setEnabled(false);
                btnOpcion3.setEnabled(false);

                int respuestaSeleccionada = respuestasUsuario.get(currentIndexQuestion.get());
                String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));

                switch (respuestaSeleccionada) {
                    case 0:
                        setBgColorBtn(btnOpcion1, btnOpcion1.getText().toString().equals(respuestaCorrecta));
                        break;
                    case 1:
                        setBgColorBtn(btnOpcion2, btnOpcion2.getText().toString().equals(respuestaCorrecta));
                        break;
                    case 2:
                        setBgColorBtn(btnOpcion3, btnOpcion3.getText().toString().equals(respuestaCorrecta));
                        break;
                }

                // Deshabilitar botón Anterior si llegamos a la primera pregunta
                btnAnterior.setEnabled(currentIndexQuestion.get() > 0);

                // Habilitar botón Siguiente
                btnSiguiente.setEnabled(true);
            }
        });
    }

    private void iniciarNuevoJuego() {
        // Reiniciar variables
        if (!hayEstadoGuardado) {
            // Solo reiniciar estas variables si no hay un estado guardado
            juegoCompletado = false;
            estadisticaGuardada = false;
            mostrandoResultado = false;

            // Iniciar nuevo cronómetro
            tiempoInicio = SystemClock.elapsedRealtime();

            // Generar preguntas aleatorias
            indices = new ArrayList<>(preguntas.keySet());
            Collections.shuffle(indices); // Mezcla los índices aleatoriamente

            // Generar opciones para cada pregunta
            listaCompletaOpciones = getListaCompletaOpciones(indices);

            // Inicializar variables del juego
            currentIndexQuestion = new AtomicInteger(0);
            optionSelected = new AtomicInteger(-1);
            respuestasUsuario = new ArrayList<>();

            // Mostrar primera pregunta
            updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()), indices, currentIndexQuestion.get(), 0);

            // Resetear el puntaje
            TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
            textoPuntaje.setText("0");
        }
    }

    private void guardarEstadoJuego() {
        hayEstadoGuardado = true;
        indexPreguntaGuardada = currentIndexQuestion.get();
        respuestasGuardadas = new ArrayList<>(respuestasUsuario);

        TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
        puntajeGuardado = Integer.parseInt(textoPuntaje.getText().toString());

        tiempoInicioGuardado = tiempoInicio;
        juegoCompletadoGuardado = juegoCompletado;
        estadisticaGuardadaFlag = estadisticaGuardada;
        mostrandoResultadoGuardado = mostrandoResultado;
    }

    private void restaurarEstadoJuego() {
        // Restaurar variables de control
        juegoCompletado = juegoCompletadoGuardado;
        estadisticaGuardada = estadisticaGuardadaFlag;
        mostrandoResultado = mostrandoResultadoGuardado;

        // Restaurar preguntas y opciones
        indices = new ArrayList<>(preguntas.keySet());
        listaCompletaOpciones = getListaCompletaOpciones(indices);

        // Restaurar estado del juego
        currentIndexQuestion = new AtomicInteger(indexPreguntaGuardada);
        optionSelected = new AtomicInteger(-1);
        respuestasUsuario = new ArrayList<>(respuestasGuardadas);
        tiempoInicio = tiempoInicioGuardado;

        // Restaurar puntaje
        TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
        textoPuntaje.setText(String.valueOf(puntajeGuardado));

        // Si estamos mostrando el resultado final
        if (mostrandoResultado) {
            mostrarResultadoSinGuardar();
            return;
        }

        // Actualizar UI para mostrar la pregunta actual
        updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()),
                indices, currentIndexQuestion.get(), respuestasUsuario.size());

        // Si esta pregunta ya fue respondida, mostrar la respuesta seleccionada
        if (currentIndexQuestion.get() < respuestasUsuario.size()) {
            Button btnOpcion1 = findViewById(R.id.btnOpcion1Microondas);
            Button btnOpcion2 = findViewById(R.id.btnOpcion2Microondas);
            Button btnOpcion3 = findViewById(R.id.btnOpcion3Microondas);

            btnOpcion1.setEnabled(false);
            btnOpcion2.setEnabled(false);
            btnOpcion3.setEnabled(false);

            int respuestaSeleccionada = respuestasUsuario.get(currentIndexQuestion.get());
            String respuestaCorrecta = respuestas.get(indices.get(currentIndexQuestion.get()));

            switch (respuestaSeleccionada) {
                case 0:
                    setBgColorBtn(btnOpcion1, btnOpcion1.getText().toString().equals(respuestaCorrecta));
                    break;
                case 1:
                    setBgColorBtn(btnOpcion2, btnOpcion2.getText().toString().equals(respuestaCorrecta));
                    break;
                case 2:
                    setBgColorBtn(btnOpcion3, btnOpcion3.getText().toString().equals(respuestaCorrecta));
                    break;
            }

            // Habilitar botón siguiente
            Button btnSiguiente = findViewById(R.id.btnSiguienteMicroondas);
            btnSiguiente.setEnabled(true);
        }

        // Habilitar/deshabilitar botón anterior según corresponda
        Button btnAnterior = findViewById(R.id.btnAnteriorMicroondas);
        btnAnterior.setEnabled(currentIndexQuestion.get() > 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Si el juego se cierra sin completarse y no es por ir a estadísticas
        // y no estamos cambiando configuración, registrar como cancelado
        if (!juegoCompletado && !estadisticaGuardada && !isChangingConfigurations() && !hayEstadoGuardado) {
            SharedPreferences prefs = getSharedPreferences("TeleQuizStats", MODE_PRIVATE);
            Estadisticas.guardarEstadistica(prefs, "Microondas", false, 0, 0);
            estadisticaGuardada = true;

            // Limpiar estado guardado
            hayEstadoGuardado = false;
        }
    }

    public ArrayList<ArrayList<String>> getListaCompletaOpciones(ArrayList<Integer> indicesAleatorios) {
        ArrayList<ArrayList<String>> listaCompletaOpciones = new ArrayList<>();
        for (int i = 0; i < indicesAleatorios.size(); i++) {
            ArrayList<String> opciones = getOpciones(indicesAleatorios.get(i));
            listaCompletaOpciones.add(opciones);
        }
        return listaCompletaOpciones;
    }

    public ArrayList<String> getOpciones(int indicePregunta) {
        ArrayList<String> opciones = new ArrayList<>();
        if (indicePregunta == 0) {
            opciones.add("2.4 GHz y 5 GHz");
            opciones.add("10 GHz y 20 GHz");
            opciones.add("900 MHz y 1.8 GHz");
        }
        if (indicePregunta == 1) {
            opciones.add("Interferencia");
            opciones.add("Sobrecarga");
            opciones.add("Oscilación");
        }
        if (indicePregunta == 2) {
            opciones.add("Área de propagación");
            opciones.add("Zona de cobertura");
            opciones.add("Región de bloqueo");
        }
        if (indicePregunta == 3) {
            opciones.add("Alta velocidad");
            opciones.add("Bajo costo");
            opciones.add("Facilidad de uso");
        }
        if (indicePregunta == 4) {
            opciones.add("Antena parabólica");
            opciones.add("Amplificador");
            opciones.add("Repetidor");
        }
        Collections.shuffle(opciones);
        return opciones;
    }

    public void updateQuestion(
            HashMap<Integer, String> preguntas,
            ArrayList<String> opciones,
            ArrayList<Integer> indicesMezclados,
            int indexQuestion,
            int cantidadRespuestasUsuario
    ) {
        TextView textoPregunta = findViewById(R.id.textoPreguntaMicroondas);
        int indiceAleatorio = indicesMezclados.get(indexQuestion);
        textoPregunta.setText((indexQuestion + 1) + ". " + preguntas.get(indiceAleatorio));

        Button btnSiguiente = findViewById(R.id.btnSiguienteMicroondas);
        Button btnOpcion1 = findViewById(R.id.btnOpcion1Microondas);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Microondas);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Microondas);

        // Asegurar que son visibles
        textoPregunta.setVisibility(View.VISIBLE);
        btnOpcion1.setVisibility(View.VISIBLE);
        btnOpcion2.setVisibility(View.VISIBLE);
        btnOpcion3.setVisibility(View.VISIBLE);

        // Resetear el color de fondo de los botones
        btnOpcion1.setBackgroundColor(getResources().getColor(R.color.gray));
        btnOpcion2.setBackgroundColor(getResources().getColor(R.color.gray));
        btnOpcion3.setBackgroundColor(getResources().getColor(R.color.gray));

        // Establecer el texto de las opciones
        btnOpcion1.setText(opciones.get(0));
        btnOpcion2.setText(opciones.get(1));
        btnOpcion3.setText(opciones.get(2));

        // Si la pregunta ya tiene respuesta, mostrarla y deshabilitar botones
        if (indexQuestion < cantidadRespuestasUsuario) {
            btnOpcion1.setEnabled(false);
            btnOpcion2.setEnabled(false);
            btnOpcion3.setEnabled(false);

            // Habilitar botón Siguiente
            btnSiguiente.setEnabled(true);
        } else {
            // Si la pregunta no tiene respuesta, habilitar opciones y deshabilitar botón Siguiente
            btnOpcion1.setEnabled(true);
            btnOpcion2.setEnabled(true);
            btnOpcion3.setEnabled(true);
            btnSiguiente.setEnabled(false);
        }

        // Ocultar la pantalla de resultado si estuviera visible
        LinearLayout layoutResultado = findViewById(R.id.layoutResultadoMicroondas);
        layoutResultado.setVisibility(View.GONE);
    }

    public void setBgColorBtn(Button btn, boolean isCorrect) {
        if (isCorrect) {
            btn.setBackgroundColor(getResources().getColor(R.color.verdeSuccess));
        } else {
            btn.setBackgroundColor(getResources().getColor(R.color.rojoFail));
        }
    }

    public void updatePuntaje(boolean isRespuestaCorrecta) {
        // PUNTAJE
        TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
        int puntajeActual = Integer.parseInt(textoPuntaje.getText().toString());
        if (isRespuestaCorrecta) {
            textoPuntaje.setText(String.valueOf(puntajeActual + 2));
        } else {
            textoPuntaje.setText(String.valueOf(puntajeActual - 2));
        }
    }

    // Método para mostrar el resultado final y guardar estadísticas
    private void mostrarResultadoFinal() {
        mostrandoResultado = true;

        // Solo guardar estadística la primera vez que se muestra el resultado
        if (!estadisticaGuardada) {
            juegoCompletado = true;
            estadisticaGuardada = true;

            // Calcular tiempo de juego
            long tiempoFin = SystemClock.elapsedRealtime();
            int tiempoTotal = (int)((tiempoFin - tiempoInicio) / 1000); // Convertir a segundos

            // Obtener puntaje final
            TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
            int puntajeFinal = Integer.parseInt(textoPuntaje.getText().toString());

            // Guardar estadística
            SharedPreferences prefs = getSharedPreferences("TeleQuizStats", MODE_PRIVATE);
            Estadisticas.guardarEstadistica(prefs, "Microondas", true, tiempoTotal, puntajeFinal);
        }

        // Mostrar el resultado en la UI
        mostrarResultadoSinGuardar();
    }

    // Método para mostrar el resultado sin guardar estadísticas (para revisión)
    private void mostrarResultadoSinGuardar() {
        mostrandoResultado = true;

        // Ocultar preguntas y opciones
        TextView textoPregunta = findViewById(R.id.textoPreguntaMicroondas);
        Button btnOpcion1 = findViewById(R.id.btnOpcion1Microondas);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Microondas);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Microondas);

        textoPregunta.setVisibility(View.GONE);
        btnOpcion1.setVisibility(View.GONE);
        btnOpcion2.setVisibility(View.GONE);
        btnOpcion3.setVisibility(View.GONE);

        // Mostrar layout de resultado
        LinearLayout layoutResultado = findViewById(R.id.layoutResultadoMicroondas);
        layoutResultado.setVisibility(View.VISIBLE);

        // Obtener y mostrar puntaje final
        TextView textoPuntaje = findViewById(R.id.textoPuntajeMicroondas);
        int puntajeFinal = Integer.parseInt(textoPuntaje.getText().toString());

        TextView txtResultadoFinal = findViewById(R.id.txtResultadoFinalMicroondas);
        txtResultadoFinal.setText(String.valueOf(puntajeFinal));

        // Configurar color de fondo según puntaje
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        if (puntajeFinal >= 0) {
            shape.setColor(getResources().getColor(R.color.verdeSuccess));
        } else {
            shape.setColor(getResources().getColor(R.color.rojoFail));
        }
        txtResultadoFinal.setBackground(shape);

        // Configurar botones de navegación
        Button btnAnterior = findViewById(R.id.btnAnteriorMicroondas);
        Button btnSiguiente = findViewById(R.id.btnSiguienteMicroondas);

        btnAnterior.setEnabled(true);  // Siempre habilitado para revisar respuestas
        btnSiguiente.setText("Siguiente");
        btnSiguiente.setEnabled(false);
    }
}