package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView address;
    private TextView mail;
    private TextView age;
    private TextView phone;
    private ImageView foto;
    private User user;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.tvName);
        address = findViewById(R.id.tvAddress);
        mail = findViewById(R.id.tvMail);
        age = findViewById(R.id.tvAge);
        phone = findViewById(R.id.tvPhone);
        foto = findViewById(R.id.fotoPerfil);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        getUser();

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                                        document.getDouble("age"),
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
}
