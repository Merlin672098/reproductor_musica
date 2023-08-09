package com.example.prueba12;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ListasGenericas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_genericas);
        String nombreLista = getIntent().getStringExtra("nombreLista");
        TextView textViewNombreLista = findViewById(R.id.nombreListas);
        textViewNombreLista.setText(nombreLista);




    }
}