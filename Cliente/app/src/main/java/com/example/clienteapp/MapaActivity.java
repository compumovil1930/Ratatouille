package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.entities.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapaActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<User> listaDatos;
    Button btnLista;
    private static final String TAG = "MapaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listaDatos = new ArrayList<>();
        btnLista = findViewById(R.id.btnLista);

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapaActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getListItems() {
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                          @Override
                                          public void onSuccess(QuerySnapshot documentSnapshots) {
                                              if (documentSnapshots.isEmpty()) {
                                                  Log.d(TAG, "onSuccess: LIST EMPTY");
                                                  return;
                                              } else {
                                                  // Convert the whole Query Snapshot to a list
                                                  // of objects directly! No need to fetch each
                                                  // document.
                                                  List<User> types = documentSnapshots.toObjects(User.class);

                                                  // Add all to your list
                                                  listaDatos.addAll(types);
                                                  Log.d(TAG, "onSuccess: " + listaDatos);
                                                  FirebaseUser us = mAuth.getCurrentUser();
                                                  if(us!=null){
                                                      System.out.println(us.getDisplayName()+ ""+listaDatos.size());
                                                      for (User u: listaDatos) {
                                                          System.out.println(""+ u.getFullName());
                                                          if(u.getFullName().equals(us.getDisplayName())){
                                                              LatLng profileLatlng = new LatLng(Float.parseFloat(u.getAddress().getLatitude()), Float.parseFloat(u.getAddress().getLongitude()));
                                                              mMap.addMarker(new MarkerOptions().position(profileLatlng).title(u.getFullName()));
                                                              mMap.moveCamera(CameraUpdateFactory.newLatLng((profileLatlng)));
                                                              mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                                          }
                                                      }
                                                  }
                                              }
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                                        }
                });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(4.65, -74.05);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        getListItems();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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
            Intent intent = new Intent(MapaActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
