package com.example.my_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.my_application.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GalleryFragment extends Fragment {

    private Spinner spinnerTags;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        spinnerTags = root.findViewById(R.id.spinner_tags);

        fetchAndPopulateSpinner();

        spinnerTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTag = spinnerTags.getSelectedItem().toString();
                Toast.makeText(requireContext(), "Selected Tag: " + selectedTag, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // pass
            }
        });

        return root;
    }

    private void fetchAndPopulateSpinner() {
        FirebaseFirestore.getInstance().collection("MyPhotos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> uniqueTags = new HashSet<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.contains("tags")) {
                            ArrayList<String> tags = (ArrayList<String>) document.get("tags");
                            if (tags != null) {
                                uniqueTags.addAll(tags);
                            }
                        }
                    }

                    ArrayList<String> tagList = new ArrayList<>(uniqueTags);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tagList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTags.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch tags: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
