package com.example.my_application.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.my_application.R;

import com.example.my_application.databinding.FragmentSlideshowBinding;

public class AboutFragment extends Fragment {

    Button btn_info;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about, container, false);

        btn_info = root.findViewById(R.id.btn_info);

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        return root;
    }

    private void sendEmail() {
        String[] TO = {"ukatib@icloud.com"}; // alıcı e-posta adresi
        String[] CC = {"cc@example.com"}; // cc e-posta adresi
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Konu"); // e-posta konusu
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Merhaba, \n\nBu bir örnek mesajdır."); // e-posta içeriği

        try {
            startActivity(Intent.createChooser(emailIntent, "E-posta gönder..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // E-posta uygulaması bulunamazsa buraya bir hata işleme kodu ekleyebilirsiniz.
        }
    }

}