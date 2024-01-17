package com.example.my_application.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_application.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AddLabelFragment extends Fragment {
    private Spinner spinnerLabels;
    private EditText etDescription;
    private Button btnAddDescription;
    private TextView tvLabels;
    private ImageView img_view_photo;

    private ArrayList<String> photoNames;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerLabels = root.findViewById(R.id.spinner_labels);
        etDescription = root.findViewById(R.id.et_description);
        btnAddDescription = root.findViewById(R.id.btn_add);
        tvLabels = root.findViewById(R.id.tv_labels);
        img_view_photo = root.findViewById(R.id.img_view_photo);

        photoNames = new ArrayList<>();

        fetchPhotoNames();

        spinnerLabels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPhotoName = spinnerLabels.getSelectedItem().toString();

                showImage(selectedPhotoName);

                String description = etDescription.getText().toString().trim();

                addDescriptionToFirestore(selectedPhotoName, description);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // pass
            }
        });

        btnAddDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedPhotoName = spinnerLabels.getSelectedItem().toString();

                String description = etDescription.getText().toString().trim();

                addDescriptionToFirestore(selectedPhotoName, description);
            }
        });

        return root;
    }

    private void fetchPhotoNames() {
        FirebaseFirestore.getInstance().collection("MyPhotos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    photoNames.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String photoName = document.getId();
                        photoNames.add(photoName);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, photoNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerLabels.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch photo names!" + e.getMessage() , Toast.LENGTH_SHORT).show();
                });
    }

    private void addDescriptionToFirestore(String photoName, String description) {
        Map<String, Object> descriptionData = new HashMap<>();
        descriptionData.put("description", description);

        FirebaseFirestore.getInstance().collection("MyPhotos")
                .document(photoName)
                .update("descriptions", FieldValue.arrayUnion(description))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Description has been added successfully :)", Toast.LENGTH_SHORT).show();
                    showDescriptions(photoName);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Description failed :(", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDescriptions(String photoName) {

        FirebaseFirestore.getInstance().collection("MyPhotos")
                .document(photoName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    ArrayList<String> descriptions = (ArrayList<String>) documentSnapshot.get("descriptions");
                    if (descriptions != null) {
                        tvLabels.setText(String.join("\n", descriptions));
                    } else {
                        tvLabels.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch label descriptions :(", Toast.LENGTH_SHORT).show();
                });
    }

    private void showImage(String photoName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos/" + photoName + ".jpg");

        storageRef.getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img_view_photo.setImageBitmap(bitmap);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch image from storage :(", Toast.LENGTH_SHORT).show();
                });
    }
}