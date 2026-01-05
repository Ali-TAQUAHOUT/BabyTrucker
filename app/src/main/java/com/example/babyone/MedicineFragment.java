package com.example.babyone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.babyone.utils.AuthHelper;
import com.example.babyone.utils.DatabaseHelper;



//    public MedicineFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_medicine, container, false);
//    }
public class MedicineFragment extends Fragment {

    private EditText editTextVaccineName;
    private EditText editTextGivenDate;
    private EditText editTextPlaceOfAdministration;
    private Button btnSubmit;
    private String username;

    public MedicineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);
        System.out.println("ONCREATE");
        // Initialize views
        editTextVaccineName = view.findViewById(R.id.edtvName);
        editTextGivenDate = view.findViewById(R.id.edtvdate);
        editTextPlaceOfAdministration = view.findViewById(R.id.edtvPlace);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Get username from email passed via intent or from current user
        Bundle args = getArguments();
        if (args != null && args.containsKey("email")) {
            username = args.getString("email");
        } else {
            username = AuthHelper.getCurrentUsername(getContext());
        }

        // Set click listener for the Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String medicineName = editTextVaccineName.getText().toString().trim();
                String givenDate = editTextGivenDate.getText().toString().trim();
                String placeOfAdministration = editTextPlaceOfAdministration.getText().toString().trim();

                // Check if all fields are filled
                if (medicineName.isEmpty() || givenDate.isEmpty() || placeOfAdministration.isEmpty()) {
                    Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add medicine to database
                boolean success = DatabaseHelper.addMedicine(getContext(), username, medicineName, givenDate, placeOfAdministration);
                if (success) {
                    Toast.makeText(getActivity(), "Médicament ajouté avec succès", Toast.LENGTH_SHORT).show();
                    // Clear the input fields
                    editTextVaccineName.setText("");
                    editTextGivenDate.setText("");
                    editTextPlaceOfAdministration.setText("");
                } else {
                    Toast.makeText(getActivity(), "Erreur lors de l'ajout du médicament", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}


