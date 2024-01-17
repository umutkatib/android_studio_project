package com.example.my_application.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.my_application.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPhotoFragment extends Fragment {

    private static final int REQUEST_CODE = 22;
    private Button take_photo;
    private ImageView img_view;
    private ArrayList<String> photoNames;
    private LinearLayout photoListLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_add_photo, container, false);

        take_photo = root.findViewById(R.id.btn_camera);
        photoListLayout = root.findViewById(R.id.photo_list_layout);
        img_view = root.findViewById(R.id.img_view);
        photoNames = new ArrayList<>();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        return root;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // uyarı verilir
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getExtras() != null) {

                Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");

                String photoName = generatePhotoName();

                String storagePath = "photos/" + photoName + ".jpg";

                photoNames.add(photoName);

                addPhotoToFirestore(photoName, storagePath);

                img_view.setImageBitmap(photoBitmap);

                updatePhotoList();

                uploadPhotoToFirebaseStorage(photoBitmap, photoName);
            }
        }
    }

    private String generatePhotoName() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "Photo_" + currentDateTime.format(formatter) + ".jpg";
    }

    private void updatePhotoList() {
        photoListLayout.removeAllViews();

        int maxTextViewCount = 6;
        int currentTextViewCount = photoListLayout.getChildCount();

        if (currentTextViewCount > maxTextViewCount) {
            photoListLayout.removeViews(maxTextViewCount, currentTextViewCount - maxTextViewCount);
        }

        for (int i = 0; i < Math.min(photoNames.size(), maxTextViewCount); i++) {
            String decoratedText = "❤ " + photoNames.get(i) + " ✓";
            TextView textView = new TextView(requireContext());
            textView.setText(decoratedText);
            photoListLayout.addView(textView);
        }
    }

    private void addPhotoToFirestore(String photoName, String storagePath) {

        Map<String, Object> photoData = new HashMap<>();
        photoData.put("photoName", photoName);
        photoData.put("storagePath", storagePath);

        FirebaseFirestore.getInstance().collection("MyPhotos")
                .document(photoName)
                .set(photoData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // belge başarıyla eklendiğinde yapılcak işlem.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // hata durumunda yapılacak işlemler
                    }
                });
    }

    private void uploadPhotoToFirebaseStorage(Bitmap photoBitmap, String photoName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        String storagePath = "photos/" + photoName + ".jpg";
        StorageReference photoRef = storageRef.child(storagePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(photoData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                String decoratedText = "❤ " + photoName + " x";
                TextView textView = new TextView(requireContext());
                textView.setText(decoratedText);
                textView.setText(decoratedText);
                Toast.makeText(requireContext(), "Fotoğraf yüklenirken hata oluştu: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(requireContext(), "Fotoğraf başarıyla yüklendi!", Toast.LENGTH_SHORT).show();

                addPhotoToFirestore(photoName, storagePath);

                img_view.setImageBitmap(photoBitmap);

                updatePhotoList();
            }
        });
    }
}