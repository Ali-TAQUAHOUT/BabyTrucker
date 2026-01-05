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

public class VaccineFragment extends Fragment {
    EditText edtHeight;
    EditText edtWeight;
    EditText edtDate;
    EditText edtvName;
    EditText edtvdate;
    EditText edtvPlace;
    Button btnSubmit;
    String username;

    public VaccineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vaccine, container, false);

        // Get views
        edtHeight = view.findViewById(R.id.edtHeight);
        edtWeight = view.findViewById(R.id.edtWeight);
        edtDate = view.findViewById(R.id.edtDate);
        edtvName = view.findViewById(R.id.edtvName);
        edtvdate = view.findViewById(R.id.edtvdate);
        edtvPlace = view.findViewById(R.id.edtvPlace);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Get username from email passed via intent or from current user
        Bundle args = getArguments();
        if (args != null && args.containsKey("email")) {
            // If email is passed, we need to find the username
            username = args.getString("email");
        } else {
            username = AuthHelper.getCurrentUsername(getContext());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String heightStr = edtHeight.getText().toString().trim();
                String weightStr = edtWeight.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String vname = edtvName.getText().toString().trim();
                String vdate = edtvdate.getText().toString().trim();
                String vplace = edtvPlace.getText().toString().trim();

                // Update weight and height
                if (!heightStr.isEmpty() && !weightStr.isEmpty() && !date.isEmpty()) {
                    try {
                        double height = Double.parseDouble(heightStr);
                        double weight = Double.parseDouble(weightStr);
                        
                        boolean success = DatabaseHelper.updateBabyWeightAndHeight(getContext(), username, weight, height, date);
                        if (success) {
                            edtWeight.setText("");
                            edtHeight.setText("");
                            edtDate.setText("");
                            Toast.makeText(getActivity(), "Poids et taille mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Veuillez entrer des valeurs numériques valides", Toast.LENGTH_SHORT).show();
                    }
                }

                // Add vaccine
                if (!vname.isEmpty() && !vdate.isEmpty() && !vplace.isEmpty()) {
                    boolean success = DatabaseHelper.addVaccine(getContext(), username, vname, vdate, vplace);
                    if (success) {
                        edtvdate.setText("");
                        edtvPlace.setText("");
                        edtvName.setText("");
                        Toast.makeText(getActivity(), "Vaccin ajouté avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Erreur lors de l'ajout du vaccin", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}
