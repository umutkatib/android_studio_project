package com.example.my_application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_application.R;
import com.example.my_application.model.UserRegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    EditText name, surname, email, password;
    Button r_login, r_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.et_r_name);
        surname = findViewById(R.id.et_r_surname);
        email = findViewById(R.id.et_r_email);
        password = findViewById(R.id.et_r_password);
        r_login = findViewById(R.id.btn_r_login);
        r_signup = findViewById(R.id.btn_r_signup);

        r_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        r_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = name.getText().toString();
                String strSurname = surname.getText().toString();
                String strEmail = email.getText().toString().toLowerCase();
                String strPassword = password.getText().toString();

                if(strName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name alanı boş olamaz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strSurname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Surname alanı boş olamaz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email alanı boş olamaz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password alanı boş olamaz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            Toast.makeText(getApplicationContext(), "Kayıt Başarıyla Gerçekleşti :)", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference ref = db.collection("usersCollection");
                            UserRegisterModel user = new UserRegisterModel(strName, strSurname, strEmail);
                            ref.add(user);

                        } else {
                            Toast.makeText(getApplicationContext(), "Kayıt Başarısız :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}