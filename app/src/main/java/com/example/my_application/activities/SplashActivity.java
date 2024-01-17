package com.example.my_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.my_application.MainActivity;
import com.example.my_application.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    Button login, signup;
    boolean btnOnClick = false;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        login = findViewById(R.id.btn_splash_login);
        signup = findViewById(R.id.btn_splash_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOnClick = true;
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOnClick = true;
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        auth = FirebaseAuth.getInstance();

        //auth.signOut();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!btnOnClick) {
                    if(auth.getCurrentUser() != null) {
                        Toast.makeText(getApplicationContext(), "Ana Sayfaya Yönlendiriliyorsunuz", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Lütfen Giriş Yapın", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                }
            }
        }, 1000);
    }
}