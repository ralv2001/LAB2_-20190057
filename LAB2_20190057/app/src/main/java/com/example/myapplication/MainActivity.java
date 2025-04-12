package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Llamamos al botón desde la vista xml
        Button btnRedes = findViewById(R.id.btnRedes);
        // Acción para cuando le doy click al botón
        btnRedes.setOnClickListener( view -> {
            Toast.makeText(this, "Botón Redes", Toast.LENGTH_SHORT).show();
            Intent intentRedes = new Intent(getApplicationContext(), JuegoRedes.class);
            startActivity(intentRedes);
        });

        Button btnCiberSeguridad = findViewById(R.id.btnCiberSeguridad);
        btnCiberSeguridad.setOnClickListener( view -> {
            Toast.makeText(this, "Botón CiberSeguridad", Toast.LENGTH_SHORT).show();
            Intent intentCiberSeguridad = new Intent(getApplicationContext(), JuegoCiberSeguridad.class);
            startActivity(intentCiberSeguridad);
        });

        Button btnMicroondas = findViewById(R.id.btnMicroondas);
        btnMicroondas.setOnClickListener( view -> {
            Toast.makeText(this, "Botón Microondas", Toast.LENGTH_SHORT).show();
        });

    }
}