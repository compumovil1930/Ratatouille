package com.example.clienteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    static final int LOCALIZACION = 101;
    static final int RADIUS_OF_EARTH_KM = 6371000;
    static final int REQUEST_CHECK_SETTINGS = 102;
    Button btnMapa;
    TextView permisoNegado;
    ListView listaChefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        btnMapa = findViewById(R.id.btnMapa);
        permisoNegado = findViewById(R.id.negado);
        listaChefs = findViewById(R.id.listaChefs);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapaActivity.class);
                startActivity(intent);
            }
        });
        requestPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION,"Ubicacion Autorizada",LOCALIZACION);
    }

    @Override
    public void onResume(){
        super .onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permisoNegado.setVisibility(View.GONE);
            btnMapa.setVisibility(View.VISIBLE);
            listaChefs.setVisibility(View.VISIBLE);
        }else{
            permisoNegado.setVisibility(View.VISIBLE);
            btnMapa.setVisibility(View.GONE);
            listaChefs.setVisibility(View.GONE);
        }
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
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case LOCALIZACION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoNegado.setVisibility(View.GONE);
                    btnMapa.setVisibility(View.VISIBLE);
                    listaChefs.setVisibility(View.VISIBLE);
                }else{
                    permisoNegado.setVisibility(View.VISIBLE);
                    btnMapa.setVisibility(View.GONE);
                    listaChefs.setVisibility(View.GONE);
                }
            }
        }
    }

    private void requestPermission (Activity context, String permiso, String justificacion, int idCode){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast toast1 =Toast.makeText(getApplicationContext(),justificacion, Toast.LENGTH_SHORT);
                toast1.show();
            }
            Log.i("prueba","AQUI VOY");
            // request the permission.
            ActivityCompat.requestPermissions(context,
                    new String[]{permiso}, idCode);
        }
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }
}
