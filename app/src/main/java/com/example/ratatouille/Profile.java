package com.example.ratatouille;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Profile extends AppCompatActivity {

    Button CS;
    Button mostrarO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        CS = findViewById(R.id.Cs);
        CS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, loginPrincipal.class);
                startActivity(intent);
            }
        });

        mostrarO = findViewById(R.id.mostrarOfertas);
        mostrarO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, GestionarOfertasEntrantes.class);
                startActivity(intent);
            }
        });
    }
}
