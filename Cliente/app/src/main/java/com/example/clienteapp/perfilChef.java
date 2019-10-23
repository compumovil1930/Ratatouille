package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entities.UserChef;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class perfilChef extends AppCompatActivity {

    private TextView nombrechef;
    private TextView distance;
    private TextView Desc;
    private TextView exp;
    private ImageView img;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_chef);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        nombrechef = findViewById(R.id.Nombrechef);
        distance = findViewById(R.id.distancechef);
        Desc = findViewById(R.id.desc);
        exp = findViewById(R.id.exp);
        img = findViewById(R.id.imagenChef);
        UserChef us = (UserChef)this.getIntent().getSerializableExtra("user");
        nombrechef.setText(""+us.getFullName());
        Double dis = this.getIntent().getExtras().getDouble("distance");
        distance.setText("Esta a "+dis+" km");
        exp.setText(us.getYearsOfExperience());
        Desc.setText(us.getBiography());
        cargarFoto(us.getUri());
    }

    public void cargarFoto(String uris){

        final StorageReference pathReference = storageRef.child("profile/"+uris);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ImageView im = findViewById(R.id.imagenChef);
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

}
