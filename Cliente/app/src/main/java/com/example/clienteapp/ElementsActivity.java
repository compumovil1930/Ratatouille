package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.entities.KitchenElement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ElementsActivity extends AppCompatActivity {

    private EditText txtName;
    private EditText txtBrand;
    private EditText txtQu;
    private Button btnAddEl;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private View.OnClickListener btnAddLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addNewElement();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements);

        inflate();
    }

    private void inflate() {
        txtName = findViewById(R.id.txtNameIt);
        txtBrand = findViewById(R.id.txtBrand);
        txtQu = findViewById(R.id.txtQu);
        txtQu.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnAddEl = findViewById(R.id.btnAddEl);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnAddEl.setOnClickListener(btnAddLis);
    }

    private void addNewElement(){
        final String name = txtName.getText().toString().trim();
        final String brand = txtBrand.getText().toString().trim();
        final String qu = txtQu.getText().toString().trim();

        if(valiidateForm(name,brand,qu)){
            KitchenElement kitchenElement = new KitchenElement(mAuth.getUid(),name,brand,Integer.parseInt(qu));
            db.collection("elements")
                    .add(kitchenElement)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(btnAddEl.getContext(),"Éxito al crear elemento", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(btnAddEl.getContext(),"Algo Falló :( ", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean valiidateForm(String name, String brand, String qu) {
        boolean flag = true;
        String numRegex = "^[0-9]*$";
        if(name.isEmpty()){
            Toast.makeText(this,"Digite Nombre", Toast.LENGTH_LONG).show();
            flag = false;
        }if(brand.isEmpty()){
            Toast.makeText(this,"Digite Marca", Toast.LENGTH_LONG).show();
            flag = false;
        }if(!qu.matches(numRegex)){
            Toast.makeText(this,"Digite Cantidad", Toast.LENGTH_LONG).show();
            flag = false;
        }
        return  flag;
    }
}