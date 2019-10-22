package com.example.clienteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.entities.Address;
import com.example.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEdad;
    private EditText txtAddr;
    private EditText txtEmail;
    private EditText txtName;
    private EditText txtPass;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String usersCollection = "users";


    private View.OnClickListener btnRegLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerNewUser();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inflateForm();
    }

    private void inflateForm() {
        mAuth = FirebaseAuth.getInstance();
        txtAddr = findViewById(R.id.txtAddr);
        txtEdad = findViewById(R.id.txtEdad);
        txtEdad.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtPass = findViewById(R.id.txtPass);
        btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(btnRegLis);
    }

    private void registerNewUser() {
        final String addr = txtAddr.getText().toString().trim();
        final String edad = txtEdad.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String name = txtName.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();
        if (validateForm(addr,edad,email,name,pass)) {

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User newUser = new User(name,Double.parseDouble(edad),email,addr);
                                db.getInstance().collection("users").document(mAuth.getUid()).set(newUser);

                            }else {
                                Toast.makeText(RegisterActivity.this, "Algo Falló :(", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }

    }

    private boolean validateForm(String addr, String age, String email, String name, String pass) {
        String numRegex = "^[0-9]*$";
        String mailRegex = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        boolean flag = true;

        if (addr.isEmpty()) {
            Toast.makeText(this,"Digite Dirección", Toast.LENGTH_LONG).show();
            flag = false;
        }else if (!age.matches(numRegex)) {
            Toast.makeText(this,"Digite Edad", Toast.LENGTH_LONG).show();
            flag = false;
        }else if (!email.matches(mailRegex)) {
            Toast.makeText(this,"Digite Correo", Toast.LENGTH_LONG).show();
            flag = false;
        }else if (name.isEmpty()) {
            Toast.makeText(this,"Digite Nombre", Toast.LENGTH_LONG).show();
            flag = false;
        }else if (pass.isEmpty()) {
            Toast.makeText(this,"Digite Contraseña", Toast.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }

}
