package com.example.my_application.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.my_application.R;
import com.example.my_application.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);

        Button btnLogout = root.findViewById(R.id.btn_logout);

        auth = FirebaseAuth.getInstance();
        auth.signOut();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser() == null) {
                    Toast.makeText(getContext(), "Giriş Sayfasına Yönlendiriliyorsunuz!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });
        return root;
    }
}