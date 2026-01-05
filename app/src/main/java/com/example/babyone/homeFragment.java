package com.example.babyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.babyone.databinding.FragmentHomeBinding;
import com.example.babyone.utils.AuthHelper;
import com.example.babyone.utils.DatabaseHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homeFragment extends Fragment {
    // Initialize variables
    /*private ImageView ivImage;
    private TextView tvName;*/
    /*private Button btLogout;*/
    private FragmentHomeBinding binding;
    private TextView txtvHeight;
    private TextView txtvWeight;
    private TextView txtvBMI;
    private TextView txtvUpcomingEvents, txtvGiven;
    private TextView txtvAge;
    private String email;
    ArrayList<Double> heightList;
    ArrayList<Double> weightList;
    String babyTimestamp;
    Period period;

    public homeFragment() {
        // Required empty public constructor
    }

    //Implement method to set email from MainLanding
    public void setEmail(String email) {
        this.email = email;
        FragmentHelper.setEmail(email);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get email/username
        if (email == null) {
            email = AuthHelper.getCurrentUsername(getContext());
        }
        
        loadData();
    }
    
    private void loadData() {

        //Binding Elements
        txtvHeight = binding.txtvHeight;
        txtvWeight = binding.txtvWeight;
        txtvBMI = binding.txtvBMI;
        txtvUpcomingEvents = binding.txtvUpcoming;
        txtvAge = binding.txtvAge;
        txtvGiven = binding.txtvGiven;

        String collectionName = "guardians/";
        DatabaseHelper.readFromCollection(getContext(), collectionName, email, new DatabaseHelper.DataCallback() {
            @Override
            public void onDataLoaded(HashMap<String, Map<String, Object>> dataMap) {
                double weight = 0;
                double height = 0;

                // Handle the retrieved data here
                for (Map.Entry<String, Map<String, Object>> entry : dataMap.entrySet()) {
                    Map<String, Object> data = entry.getValue();
                    for (Map.Entry<String, Object> fieldEntry : data.entrySet()) {
                        String fieldName = fieldEntry.getKey();
                        Object fieldValue = fieldEntry.getValue();
                        
                        //Retrive baby height
                        if (fieldName.equals("current_height") && fieldValue != null) {
                            height = ((Number) fieldValue).doubleValue();
                            txtvHeight.setText(String.format("%.1f", height) + "cm");
                        }
                        //Retrive baby weight
                        if (fieldName.equals("current_weight") && fieldValue != null) {
                            weight = ((Number) fieldValue).doubleValue();
                            txtvWeight.setText(String.format("%.1f", weight) + "kg");
                        }
                        if (fieldName.equals("baby_bday")) {
                            babyTimestamp = fieldValue.toString();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate babyBirthday = LocalDate.parse(babyTimestamp, formatter);
                            LocalDate currentDate = LocalDate.now();
                            period = Period.between(babyBirthday, currentDate);
                            int years = period.getYears();
                            int months = period.getMonths();
                            int days = period.getDays();
                            txtvAge.setText(years+"Y " + months+"M " + days+"D");
                        }
                    }
                }

                // Calculate BMI
                if (height > 0 && weight > 0) {
                    double heightInMeter = height / 100.0; // Convert height to meters
                    double bmi = weight / Math.pow(heightInMeter, 2);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String formattedBMI = decimalFormat.format(bmi);
                    txtvBMI.setText(formattedBMI);
                }

                // Load vaccines
                String subcollectionName = "vaccines";
                DatabaseHelper.readFromSubcollection(getContext(), "guardians", email, subcollectionName, new DatabaseHelper.DataCallback() {
                    @Override
                    public void onDataLoaded(HashMap<String, Map<String, Object>> dataMap) {
                        StringBuilder upcomingEvents = new StringBuilder();
                        upcomingEvents.append("                   Upcoming Events\n--------------------------------------\n");
                        for (Map.Entry<String, Map<String, Object>> entry : dataMap.entrySet()) {
                            Map<String, Object> data = entry.getValue();
                            String date = data.get("date_administered") != null ? (String) data.get("date_administered") : "Pending";
                            String name = (String) data.get("vaccine_name");
                            String formattedText = String.format("%-30s - %s\n", name, date);
                            upcomingEvents.append(formattedText);
                        }
                        txtvUpcomingEvents.setText(upcomingEvents.toString());
                    }
                });
                
                // Load medicines
                subcollectionName = "medicines";
                DatabaseHelper.readFromSubcollection(getContext(), "guardians", email, subcollectionName, new DatabaseHelper.DataCallback() {
                    @Override
                    public void onDataLoaded(HashMap<String, Map<String, Object>> dataMap) {
                        StringBuilder givenMedicines = new StringBuilder();
                        givenMedicines.append("           Given Medicine and Vaccines\n-----------------------------------------\n");
                        for (Map.Entry<String, Map<String, Object>> entry : dataMap.entrySet()) {
                            Map<String, Object> data = entry.getValue();
                            String medicineName = (String) data.get("medicine_name");
                            String startDate = data.get("start_date") != null ? (String) data.get("start_date") : "";
                            String formattedText = String.format("%-30s - %s\n", medicineName, startDate);
                            givenMedicines.append(formattedText);
                        }
                        txtvGiven.setText(givenMedicines.toString());
                    }
                });
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when fragment becomes visible
        if (email != null) {
            loadData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

