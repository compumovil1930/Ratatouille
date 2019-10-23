package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    static final int IMAGE_PICKER_REQUEST = 102;
    static final int STORAGE = 101;

    private TextView name;
    private TextView address;
    private TextView mail;
    private TextView age;
    private TextView phone;
    private ImageView foto;
    private User user;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        name = findViewById(R.id.tvName);
        address = findViewById(R.id.tvAddress);
        mail = findViewById(R.id.tvMail);
        age = findViewById(R.id.tvAge);
        phone = findViewById(R.id.tvPhone);
        foto = findViewById(R.id.fotoPerfil);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE,"Se necesita el permiso de la galeria para usar esta funcionalidad",STORAGE);
                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                } else {
                    Toast.makeText(ProfileActivity.this,"No tiene permiso para acceder a esta funcionalidad, Por favor activarlos en Ajustes",Toast.LENGTH_LONG).show();
                }
            }
        });

        getUser();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        switch (itemClicked){
            case R.id.cerrarSesion: {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            case R.id.perfil: {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission (Activity context, String permiso, String justificacion, int idCode){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast toast1 = Toast.makeText(getApplicationContext(), justificacion, Toast.LENGTH_SHORT);

                toast1.show();
            }
            // request the permission.
            ActivityCompat.requestPermissions(context,
                    new String[]{permiso}, idCode);
        }
    }

    public void getUser(){

        DocumentReference docRef = db.collection("users").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = new User(document.getString("fullName"),
                                        document.getDouble("age").intValue(),
                                        document.getString("email"),
                                        document.getString("address"));
                        name.setText(user.getFullName());
                        mail.setText(user.getEmail());
                        age.setText(String.valueOf(user.getAge()));
                        address.setText(user.getAddress());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                        StorageReference riversRef = storageRef.child("profile/"+imageUri.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(imageUri);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("TAGO",exception.getMessage());
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("TAGA",taskSnapshot.getMetadata().getName());
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                            }
                        });

                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        foto.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
             default:
                 return;

        }
    }
}
