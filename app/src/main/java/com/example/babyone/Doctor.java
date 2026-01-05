package com.example.babyone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babyone.utils.AuthHelper;
import com.example.babyone.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class Doctor extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private BabyAdapter adapter;
    private Button btnLogout;
    private TextView welcomeDoctor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        searchView = findViewById(R.id.searchView);
        /*card1 = findViewById(R.id.card1);*/
        recyclerView = findViewById(R.id.babyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnLogout = findViewById(R.id.btnLogOut);
        welcomeDoctor = findViewById(R.id.welcomeDoctor);

        // Get the current user
        String username = AuthHelper.getCurrentUsername(this);
        if (username != null) {
            Map<String, Object> doctorInfo = DatabaseHelper.getDoctorInfo(this, username);
            if (doctorInfo != null) {
                String doctorName = (String) doctorInfo.get("name");
                welcomeDoctor.setText("Welcome Back!\n Dr. " + doctorName);
            }
        }

        adapter = new BabyAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    loadAllGuardians();
                } else {
                    List<Map<String, Object>> results = DatabaseHelper.searchGuardiansByName(Doctor.this, query);
                    List<Guardian> guardians = new ArrayList<>();
                    for (Map<String, Object> result : results) {
                        String babyName = (String) result.get("babyname");
                        String parentName = result.get("parentname") != null ? (String) result.get("parentname") : "";
                        String email = result.get("email") != null ? (String) result.get("email") : "";
                        guardians.add(new Guardian(babyName, parentName, email));
                    }
                    adapter.updateData(guardians);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty()) {
                    loadAllGuardians();
                } else {
                    List<Map<String, Object>> results = DatabaseHelper.searchGuardiansByName(Doctor.this, query);
                    List<Guardian> guardians = new ArrayList<>();
                    for (Map<String, Object> result : results) {
                        String babyName = (String) result.get("babyname");
                        String parentName = result.get("parentname") != null ? (String) result.get("parentname") : "";
                        String email = result.get("email") != null ? (String) result.get("email") : "";
                        guardians.add(new Guardian(babyName, parentName, email));
                    }
                    adapter.updateData(guardians);
                }
                return false;
            }

        });

        /*card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MainLandin activity
                // Assuming you have a button or click event to navigate to MainLanding
                Intent intent = new Intent(Doctor.this, MainLanding.class);
                intent.putExtra("sourceFragment", "doctor");
                startActivity(intent);
            }
        });*/

        // Load all guardians initially
        loadAllGuardians();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthHelper.logout(Doctor.this);
                Intent intent = new Intent(Doctor.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadAllGuardians() {
        List<Map<String, Object>> results = DatabaseHelper.getAllGuardians(this);
        List<Guardian> guardians = new ArrayList<>();
        for (Map<String, Object> result : results) {
            String babyName = (String) result.get("babyname");
            String parentName = result.get("parentname") != null ? (String) result.get("parentname") : "";
            String email = result.get("email") != null ? (String) result.get("email") : "";
            guardians.add(new Guardian(babyName, parentName, email));
        }
        adapter = new BabyAdapter(guardians, this);
        recyclerView.setAdapter(adapter);



    }
}