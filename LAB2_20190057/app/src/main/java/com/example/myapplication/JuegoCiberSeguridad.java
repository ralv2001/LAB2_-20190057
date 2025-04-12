package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JuegoCiberSeguridad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_juego_ciber_seguridad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton goBack = findViewById(R.id.btnToMain);
        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        // ACTUALIZACIÓN DE PREGUNTA EN LA VISTA
        // Lista de preguntas
        HashMap<Integer, String> preguntas = new HashMap<>();
        preguntas.put(0, "¿Qué es un Ransomware?");
        preguntas.put(1, "¿Cuál es el objetivo del phishing?");
        preguntas.put(2, "¿Qué protocolo cifra las comunicaciones web?");
        preguntas.put(3, "¿Qué algoritmo de cifrado es asimétrico y se usa comúnmente para firmas digitales?");
        preguntas.put(4, "¿Para qué sirve un firewall?");

        // Lista de respuestas correctas
        HashMap<Integer, String> respuestas = new HashMap<>();
        respuestas.put(0, "Malware de secuestro");
        respuestas.put(1, "Robar credenciales");
        respuestas.put(2, "HTTPS");
        respuestas.put(3, "RSA");
        respuestas.put(4, "Filtrar tráfico");

        // Lista de indices aleatorios
        ArrayList<Integer> indices = new ArrayList<>(preguntas.keySet());
        Collections.shuffle(indices); // Mezcla los índices aleatoriamente

        // Tabla de opciones aleatorias para cada pregunta
        ArrayList<ArrayList<String>> listaCompletaOpciones = getListaCompletaOpciones(indices);

        AtomicInteger currentIndexQuestion = new AtomicInteger(0);
        updateQuestion(preguntas, listaCompletaOpciones.get(currentIndexQuestion.get()), indices, currentIndexQuestion.get(), 0);
        // END ACTUALIZACIÓN DE PREGUNTA EN LA VISTA

        AtomicInteger optionSelected = new AtomicInteger(-1);
        Button btnOpcion1 = findViewById(R.id.btnOpcion1Ciber);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Ciber);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Ciber);
        Button btnSiguiente = findViewById(R.id.btnSiguienteCiber);
        Button btnAnterior = findViewById(R.id.btnAnteriorCiber);

        // Lista de Respuestas del usuario
        ArrayList<Integer> respuestasUsuario = new ArrayList<>();
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
            LinearLayout layoutResultado = findViewById(R.id.layoutResultadoCiber);
            if (layoutResultado.getVisibility() == View.VISIBLE) {
                layoutResultado.setVisibility(View.GONE);

                TextView textoPregunta = findViewById(R.id.textoPreguntaCiber);
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
            opciones.add("Malware de secuestro");
            opciones.add("Virus común");
            opciones.add("Software espía");
        }
        if (indicePregunta == 1) {
            opciones.add("Robar credenciales");
            opciones.add("Instalar virus");
            opciones.add("Crear backdoors");
        }
        if (indicePregunta == 2) {
            opciones.add("HTTPS");
            opciones.add("FTP");
            opciones.add("SMTP");
        }
        if (indicePregunta == 3) {
            opciones.add("RSA");
            opciones.add("AES");
            opciones.add("DES");
        }
        if (indicePregunta == 4) {
            opciones.add("Filtrar tráfico");
            opciones.add("Acelerar internet");
            opciones.add("Comprimir datos");
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
        TextView textoPregunta = findViewById(R.id.textoPreguntaCiber);
        int indiceAleatorio = indicesMezclados.get(indexQuestion);
        textoPregunta.setText(preguntas.get(indiceAleatorio));

        Button btnSiguiente = findViewById(R.id.btnSiguienteCiber);
        Button btnOpcion1 = findViewById(R.id.btnOpcion1Ciber);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Ciber);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Ciber);

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
        LinearLayout layoutResultado = findViewById(R.id.layoutResultadoCiber);
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
        TextView textoPuntaje = findViewById(R.id.textoPuntajeCiber);
        int puntajeActual = Integer.parseInt(textoPuntaje.getText().toString());
        if (isRespuestaCorrecta) {
            textoPuntaje.setText(String.valueOf(puntajeActual + 2));
        } else {
            textoPuntaje.setText(String.valueOf(puntajeActual - 2));
        }
    }

    private void mostrarResultadoFinal() {
        // Ocultar preguntas y opciones
        TextView textoPregunta = findViewById(R.id.textoPreguntaCiber);
        Button btnOpcion1 = findViewById(R.id.btnOpcion1Ciber);
        Button btnOpcion2 = findViewById(R.id.btnOpcion2Ciber);
        Button btnOpcion3 = findViewById(R.id.btnOpcion3Ciber);

        textoPregunta.setVisibility(View.GONE);
        btnOpcion1.setVisibility(View.GONE);
        btnOpcion2.setVisibility(View.GONE);
        btnOpcion3.setVisibility(View.GONE);

        // Mostrar layout de resultado
        LinearLayout layoutResultado = findViewById(R.id.layoutResultadoCiber);
        layoutResultado.setVisibility(View.VISIBLE);

        // Obtener y mostrar puntaje final
        TextView textoPuntaje = findViewById(R.id.textoPuntajeCiber);
        int puntajeFinal = Integer.parseInt(textoPuntaje.getText().toString());

        TextView txtResultadoFinal = findViewById(R.id.txtResultadoFinalCiber);
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
        Button btnAnterior = findViewById(R.id.btnAnteriorCiber);
        Button btnSiguiente = findViewById(R.id.btnSiguienteCiber);

        btnAnterior.setEnabled(true);  // Siempre habilitado para revisar respuestas
        btnSiguiente.setText("Siguiente");
        btnSiguiente.setEnabled(false);
    }
}