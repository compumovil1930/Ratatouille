package com.example.clienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;


public class ChooseService extends AppCompatActivity {

    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    TextView tipo;
    Spinner servicio;
    Spinner comida;
    Button buscar;
    TextView fecha;
    TextView laFecha;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service);

        tipo = findViewById(R.id.tvComida);
        servicio = findViewById(R.id.spServicio);
        comida = findViewById(R.id.spComida);
        buscar = findViewById(R.id.btnBuscar);
        fecha = findViewById(R.id.tvFecha);
        laFecha = findViewById(R.id.lFecha);
        mAuth = FirebaseAuth.getInstance();

        tipo.setVisibility(View.GONE);
        comida.setVisibility(View.GONE);
        fecha.setVisibility(View.GONE);
        laFecha.setVisibility(View.GONE);

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerFecha();
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseService.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        servicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Spinner",""+i);
                switch (i){
                    case 0:{
                        tipo.setVisibility(View.VISIBLE);
                        comida.setVisibility(View.VISIBLE);
                        laFecha.setVisibility(View.GONE);
                        fecha.setVisibility(View.GONE);
                        break;
                    }
                    case 2:{
                        fecha.setVisibility(View.VISIBLE);
                        tipo.setVisibility(View.VISIBLE);
                        comida.setVisibility(View.VISIBLE);
                        laFecha.setVisibility(View.VISIBLE);
                        break;
                    }
                    default:{
                        tipo.setVisibility(View.GONE);
                        comida.setVisibility(View.GONE);
                        fecha.setVisibility(View.GONE);
                        laFecha.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.cerrarSesion){
            mAuth.signOut();
            Intent intent = new Intent(ChooseService.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

}
