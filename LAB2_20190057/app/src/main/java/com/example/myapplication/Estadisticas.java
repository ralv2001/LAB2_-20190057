package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Estadisticas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar botón de regreso
        ImageButton btnBack = findViewById(R.id.btnBackFromStats);
        btnBack.setOnClickListener(v -> {
            // Obtener la actividad de origen y volver a ella
            String origin = getIntent().getStringExtra("origin");
            if (origin != null) {
                try {
                    Class<?> originClass = Class.forName(origin);
                    Intent intent = new Intent(getApplicationContext(), originClass);
                    // Pasar flag para indicar que no debe reiniciar el juego
                    intent.putExtra("fromStats", true);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    // Si ocurre un error, volver a MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            } else {
                // Si no hay origen, volver a MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        // Cargar y mostrar estadísticas
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        LinearLayout listaEstadisticas = findViewById(R.id.listaEstadisticas);
        listaEstadisticas.removeAllViews(); // Limpiar vistas previas

        // Obtener estadísticas guardadas
        SharedPreferences prefs = getSharedPreferences("TeleQuizStats", MODE_PRIVATE);
        String statsJson = prefs.getString("estadisticas", "[]");

        try {
            JSONArray statsArray = new JSONArray(statsJson);

            // Si no hay estadísticas, mostrar mensaje
            if (statsArray.length() == 0) {
                TextView noStats = new TextView(this);
                noStats.setText("No hay juegos registrados aún");
                noStats.setPadding(0, 20, 0, 20);
                noStats.setTextSize(16);
                noStats.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                listaEstadisticas.addView(noStats);
                return;
            }

            // Mostrar cada entrada de estadística
            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject gameStats = statsArray.getJSONObject(i);
                String tema = gameStats.getString("tema");
                boolean completado = gameStats.getBoolean("completado");

                TextView gameEntry = new TextView(this);
                gameEntry.setPadding(15, 20, 15, 20);
                gameEntry.setTextSize(16);

                if (completado) {
                    int tiempo = gameStats.getInt("tiempo");
                    int puntaje = gameStats.getInt("puntaje");
                    gameEntry.setText("Juego " + (i+1) + ": " + tema + " | Tiempo: " + tiempo + "s | Puntaje: " + puntaje);

                    // Color según puntaje (verde si es positivo, rojo si es negativo)
                    if (puntaje >= 0) {
                        gameEntry.setTextColor(getResources().getColor(R.color.verdeSuccess));
                    } else {
                        gameEntry.setTextColor(getResources().getColor(R.color.rojoFail));
                    }
                } else {
                    gameEntry.setText("Juego " + (i+1) + ": Canceló");
                    gameEntry.setTextColor(Color.GRAY);
                }

                // Agregar línea separadora
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2)); // 2dp de altura
                divider.setBackgroundColor(Color.LTGRAY);

                listaEstadisticas.addView(gameEntry);
                if (i < statsArray.length() - 1) {
                    listaEstadisticas.addView(divider);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            // En caso de error, mostrar mensaje
            TextView errorText = new TextView(this);
            errorText.setText("Error al cargar estadísticas");
            errorText.setTextColor(Color.RED);
            listaEstadisticas.addView(errorText);
        }
    }

    // Método estático para guardar estadísticas desde cualquier actividad
    public static void guardarEstadistica(
            SharedPreferences prefs,
            String tema,
            boolean completado,
            int tiempo,
            int puntaje) {

        try {
            // Obtener array de estadísticas actual o crear uno nuevo
            String statsJson = prefs.getString("estadisticas", "[]");
            JSONArray statsArray = new JSONArray(statsJson);

            // Crear nuevo objeto para esta partida
            JSONObject gameStats = new JSONObject();
            gameStats.put("tema", tema);
            gameStats.put("completado", completado);
            if (completado) {
                gameStats.put("tiempo", tiempo);
                gameStats.put("puntaje", puntaje);
            }

            // Agregar al principio del array (más reciente primero)
            JSONArray newStatsArray = new JSONArray();
            newStatsArray.put(gameStats);

            // Agregar elementos anteriores (hasta un máximo de 10)
            for (int i = 0; i < Math.min(statsArray.length(), 9); i++) {
                newStatsArray.put(statsArray.get(i));
            }

            // Guardar
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("estadisticas", newStatsArray.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}