package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entities.Address;
import com.example.entities.User;
import com.example.entities.UserChef;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class listC extends AppCompatActivity {

    ListView lv;
    TextView tv;
    LinearLayout layout;
    PopupWindow popup;
    private FirebaseFirestore db;
    private ArrayList<UserChef> listaDatos;
    private ArrayList<UserChef> escogidos;
    private static final String TAG = "listCActivity";
    private FirebaseAuth mAuth;
    private User user;
    public static final int RADIUS_OF_EARTH_KM = 6371;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_c);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = new User();
        getUser();
        listaDatos = new ArrayList<>();
        escogidos = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        lv = findViewById(R.id.lista);
        tv = findViewById(R.id.msgChefs);
        getListItems();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                          Log.d(TAG, "onSuccess: profile chef");
                                          Intent intent = new Intent(getBaseContext(), perfilChef.class);
                                          intent.putExtra("user", escogidos.get(i));
                                          intent.putExtra("distance", distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), escogidos.get(i).getAddress().getLatitude(), escogidos.get(i).getAddress().getLongitude()));
                                          startActivity(intent);
                                      }
                                  });
        //escogidos.addAll(getListItems());
       // lv.setAdapter(new ChefAdapter(getBaseContext(), R.layout.layoutlista, escogidos));
       // Log.d(TAG, "onSuccess: LIST EMPTY"+escogidos.size());

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return escogidos.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                view = getLayoutInflater().inflate(R.layout.layoutlista, viewGroup,  false);
            }
            TextView tname = view.findViewById(R.id.Nombre);
            TextView tdesc = view.findViewById(R.id.desc);
            TextView tdis = view.findViewById(R.id.distance);
            ImageView im = findViewById(R.id.imagenPersonas);
            Log.d(TAG, "onSuccess: LIST EMPTY");
            Log.d(TAG, "onSuccess: LIST EMPTY");
            Log.d(TAG, "onSuccess: LIST EMPTY");
            cargarFoto(escogidos.get(i).getUri(), view);
            tname.setText(escogidos.get(i).getFullName());
            tdesc.setText(escogidos.get(i).getBiography());
            tdis.setText("Se encuentra a: "+distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), escogidos.get(i).getAddress().getLatitude(), escogidos.get(i).getAddress().getLongitude()));
            return view;
        }
    }

    public class ChefAdapter extends ArrayAdapter<UserChef> {
        private int resourceLayout;
        private Context mContext;

        public ChefAdapter(Context context, int resource, ArrayList<UserChef> items) {
            super(context, resource, items);
            this.resourceLayout = resource;
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(mContext);
                v = vi.inflate(resourceLayout, null);
            }

            UserChef p = getItem(position);

            if (p != null) {
                TextView tname = v.findViewById(R.id.Nombre);
                TextView tdesc = v.findViewById(R.id.desc);
                TextView tdis = v.findViewById(R.id.distance);
                cargarFoto(p.getUri(), v);

                tname.setText(p.getFullName());
                tdesc.setText(p.getBiography());
                tdis.setText("a " + distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), p.getAddress().getLatitude(), p.getAddress().getLongitude())+"km");
            }
            return v;
        }
    }

    private ArrayList<UserChef> getListItems() {
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            tv.setText("Lo siento, no tienes chefs cerca de tu ubicación!");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<UserChef> types = documentSnapshots.toObjects(UserChef.class);

                            // Add all to your list
                            listaDatos.addAll(types);
                            Log.d(TAG, "onSuccess: " + listaDatos);
                            //recorre la lista de todos los users
                            for (UserChef uc :listaDatos){
                                //filtro por chefs
                                if(uc.getType().equals("Chef")){
                                    if(distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), uc.getAddress().getLatitude(), uc.getAddress().getLongitude())<=5){
                                        //chefs en menos de 5km, todos
                                        System.out.println("CERCA!!");
                                        escogidos.add(uc);
                                    }
                                    else{
                                        System.out.println("LEJOS!!");
                                    }
                                }
                            }
                            tv.setText("Hay "+escogidos.size()+" chefs cerca de ti!");
                            ordenarChefs(escogidos);
                            lv.setAdapter(new ChefAdapter(getBaseContext(), R.layout.layoutlista, escogidos));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
        Log.d(TAG, "onSuccess: LIST EMPTY"+escogidos.size());
        return escogidos;
    }

    public void getUser(){
        Log.d("TAGA", "No such document");
        Log.d("TAGA", "No such document");
        Log.d("TAGA", "No such document");
        Log.d("TAGA", "No such document");
        DocumentReference docRef = db.collection("users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("TAGA", "No such documents");
                Log.d("TAGA", "No such documents");
                if (task.isSuccessful()) {
                    Log.d("TAGA", "No such document1");
                    Log.d("TAGA", "No such document1");
                    DocumentSnapshot document = task.getResult();
                    System.out.println("User search...");
                    if (document.exists()) {
                        user.setFullName(document.getString("fullName"));
                        user.setAge(document.getDouble("age").intValue());
                        user.setEmail(document.getString("email"));
                        Address add = new Address();
                        add.setAddress(document.getString("address.address"));
                        add.setLatitude(document.getDouble("address.latitude"));
                        add.setLongitude(document.getDouble("address.longitude"));
                        System.out.println("address: "+add.getAddress()+add.getLatitude()+add.getLongitude());
                        user.setAddress(add);
                        Log.d("TAGA", "DocumentSnapshot data: " + document.getString("fullName"));
                    } else {
                        Log.d("TAGA", "No such document");
                    }
                } else {
                    Log.d("TAGA", "get failed with ", task.getException());
                }
            }
        });

    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance= Math.toRadians(lat1 -lat2);
        double lngDistance= Math.toRadians(long1 -long2);
        double a = Math.sin(latDistance/ 2) * Math.sin(latDistance/ 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance/ 2) * Math.sin(lngDistance/ 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 -a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    public void cargarFoto(String uris, final View v){

        final StorageReference pathReference = storageRef.child("profile/"+uris);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ImageView im = v.findViewById(R.id.imagenPersonas);
                    im.setImageBitmap(bitmap);
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }catch (Exception e){
        }
    }

    public void ordenarChefs(ArrayList escogidos){
        if(!escogidos.isEmpty()){
            System.out.println("Listado lleno!");
            Collections.sort(escogidos, new Comparator<UserChef>(){
                public int compare(UserChef obj1, UserChef obj2) {

                    Double distancia1 = distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), obj1.getAddress().getLatitude(), obj1.getAddress().getLongitude());
                    Double distancia2 = distance(user.getAddress().getLatitude(), user.getAddress().getLongitude(), obj2.getAddress().getLatitude(), obj2.getAddress().getLongitude());
                    return distancia1.compareTo(distancia2);
                }
            });
        }
    }
}
