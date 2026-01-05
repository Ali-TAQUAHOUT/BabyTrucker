package com.example.babyone;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babyone.database.BabyDatabase;
import com.example.babyone.database.dao.EventDao;
import com.example.babyone.database.dao.GuardianDao;
import com.example.babyone.database.dao.UserDao;
import com.example.babyone.utils.AuthHelper;
import com.example.babyone.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManagementActivity extends AppCompatActivity {
    private EditText edtEventName, edtEventDate, edtEventDescription;
    private Button btnAddEvent, btnUpdateEvent, btnDeleteEvent;
    private ListView listViewEvents;
    private List<Map<String, Object>> eventsList;
    private long guardianId;
    private String guardianUsername;
    private android.widget.ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_management);

        edtEventName = findViewById(R.id.edtEventName);
        edtEventDate = findViewById(R.id.edtEventDate);
        edtEventDescription = findViewById(R.id.edtEventDescription);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
        listViewEvents = findViewById(R.id.listViewEvents);

        // Get guardian username from intent
        guardianUsername = getIntent().getStringExtra("guardian_username");
        if (guardianUsername == null) {
            Toast.makeText(this, "Erreur: Username du bébé non trouvé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load guardian ID
        loadGuardianId();
        
        // Load events
        loadEvents();

        btnAddEvent.setOnClickListener(v -> addEvent());
        btnUpdateEvent.setOnClickListener(v -> updateEvent());
        btnDeleteEvent.setOnClickListener(v -> deleteEvent());

        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> event = eventsList.get(position);
            edtEventName.setText((String) event.get("event_name"));
            edtEventDate.setText((String) event.get("event_date"));
            String description = (String) event.get("description");
            if (description != null) {
                edtEventDescription.setText(description);
            }
        });
    }

    private void loadGuardianId() {
        guardianId = DatabaseHelper.getGuardianIdByUsername(this, guardianUsername);
    }

    private void loadEvents() {
        if (guardianId <= 0) {
            return;
        }

        BabyDatabase dbHelper = new BabyDatabase(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            EventDao eventDao = new EventDao(db);
            eventsList = eventDao.getEventsByGuardianId(guardianId);
            
            List<String> eventStrings = new ArrayList<>();
            for (Map<String, Object> event : eventsList) {
                String name = (String) event.get("event_name");
                String date = (String) event.get("event_date");
                eventStrings.add(name + " - " + date);
            }
            
            adapter = new android.widget.ArrayAdapter<>(this, 
                    android.R.layout.simple_list_item_1, eventStrings);
            listViewEvents.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du chargement des événements", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void addEvent() {
        String eventName = edtEventName.getText().toString().trim();
        String eventDate = edtEventDate.getText().toString().trim();
        String description = edtEventDescription.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir le nom et la date", Toast.LENGTH_SHORT).show();
            return;
        }

        BabyDatabase dbHelper = new BabyDatabase(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            EventDao eventDao = new EventDao(db);
            String currentUser = AuthHelper.getCurrentUsername(this);
            eventDao.insertEvent(guardianId, eventName, eventDate, "custom", description, currentUser);
            Toast.makeText(this, "Événement ajouté avec succès", Toast.LENGTH_SHORT).show();
            
            // Clear fields
            edtEventName.setText("");
            edtEventDate.setText("");
            edtEventDescription.setText("");
            
            // Reload events
            loadEvents();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'ajout de l'événement", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void updateEvent() {
        if (listViewEvents.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Veuillez sélectionner un événement", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = listViewEvents.getSelectedItemPosition();
        Map<String, Object> event = eventsList.get(position);
        long eventId = (Long) event.get("id");

        String eventName = edtEventName.getText().toString().trim();
        String eventDate = edtEventDate.getText().toString().trim();
        String description = edtEventDescription.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir le nom et la date", Toast.LENGTH_SHORT).show();
            return;
        }

        BabyDatabase dbHelper = new BabyDatabase(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            EventDao eventDao = new EventDao(db);
            eventDao.updateEvent(eventId, eventName, eventDate, "custom", description);
            Toast.makeText(this, "Événement mis à jour avec succès", Toast.LENGTH_SHORT).show();
            
            // Clear fields
            edtEventName.setText("");
            edtEventDate.setText("");
            edtEventDescription.setText("");
            
            // Reload events
            loadEvents();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la mise à jour de l'événement", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    private void deleteEvent() {
        if (listViewEvents.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Veuillez sélectionner un événement", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = listViewEvents.getSelectedItemPosition();
        Map<String, Object> event = eventsList.get(position);
        long eventId = (Long) event.get("id");

        BabyDatabase dbHelper = new BabyDatabase(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            EventDao eventDao = new EventDao(db);
            eventDao.deleteEvent(eventId);
            Toast.makeText(this, "Événement supprimé avec succès", Toast.LENGTH_SHORT).show();
            
            // Clear fields
            edtEventName.setText("");
            edtEventDate.setText("");
            edtEventDescription.setText("");
            
            // Reload events
            loadEvents();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la suppression de l'événement", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}



