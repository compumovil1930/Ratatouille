package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.entities.KitchenElement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementsActivity extends AppCompatActivity {

    private LinearLayout auxScrlLyt;
    private Button btnAddEl;
    private Map<String, CheckBox> filler = new HashMap<>();
    Map<String, Object> map = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private View.OnClickListener btnAddLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addElements();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements);
        inflate();
        getElementsDB();
    }

    private void inflate() {
        auxScrlLyt = findViewById(R.id.auxScrlLyt);
        btnAddEl = findViewById(R.id.btnAddEl);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnAddEl.setOnClickListener(btnAddLis);
    }

    private void getElementsDB(){
        db.collection("utensils").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                map = document.getData();
                                fillCheckboxes(map);
                            }
                        } else {
                            Toast.makeText(btnAddEl.getContext(),"Algo Falló :( complete", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addElements(){
        int totalEl = auxScrlLyt.getChildCount();
        List<DocumentReference> utensils = new ArrayList<>();
        for(Map.Entry<String, CheckBox> entry : filler.entrySet()){
            if(entry.getValue().isSelected()){
                utensils.add(db.collection("utensils").document(entry.getKey()));
            }
        }
        /*for(int i = 0; i< totalEl; i++ ){
            if(auxScrlLyt.getChildAt(i).isSelected()){
                Toast.makeText(btnAddEl.getContext(), "TRUE", Toast.LENGTH_LONG).show();
                CheckBox tmp = (CheckBox) auxScrlLyt.getChildAt(i);
                String idUt = getKey(map,tmp.getText().toString());
                utensils.add(db.collection("utensils").document(idUt));
            }
        }*/
        Toast.makeText(btnAddEl.getContext(), "Utensils: "+utensils.size(), Toast.LENGTH_LONG).show();
        db.collection("users").document(mAuth.getUid()).update("utensils",utensils);

    }

    private static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void fillCheckboxes(Map<String, Object> map){
        for(Map.Entry<String, Object> entry : map.entrySet()){
            CheckBox tmp = new CheckBox(auxScrlLyt.getContext());
            tmp.setText(entry.getValue().toString());
            filler.put(entry.getKey(), tmp);
        }
        for(Map.Entry<String, CheckBox> entry : filler.entrySet()){
            auxScrlLyt.addView(entry.getValue());
        }
    }

}