package com.example.ratatouille;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class seleccionEvento extends AppCompatActivity {

    ImageButton f1;
    ImageButton f2;
    ImageButton f3;
    ImageButton f4;

    Button b1;
    Button b2;
    Button b3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_evento);

        f1 = findViewById(R.id.fotoShow);
        f2 = findViewById(R.id.fotoChef);
        f3 = findViewById(R.id.fotoNose);
        f4 = findViewById(R.id.fotoPerfil);

        b1 = findViewById(R.id.bShow);
        b2 = findViewById(R.id.bChef);
        b3 = findViewById(R.id.bNose);

        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(seleccionEvento.this, CreateOrderUser.class);
                startActivity(intent);
            }
        });
        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, GestionarOfertasEntrantes.class);
                startActivity(intent);

            }
        });
        f3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, GestionarOfertasEntrantes.class);
                startActivity(intent);

            }
        });
        f4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, Profile.class);
                startActivity(intent);

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, CreateOrderUser.class);
                startActivity(intent);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, GestionarOfertasEntrantes.class);
                startActivity(intent);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(seleccionEvento.this, CreateOrderUser.class);
                startActivity(intent);
            }
        });
    }
}
