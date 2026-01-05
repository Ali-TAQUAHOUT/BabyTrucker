package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccineDao {
    private SQLiteDatabase db;

    public VaccineDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertVaccine(long guardianId, String vaccineName, Integer weeksFromBirth, 
                             String dateAdministered, String status, String notes) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("vaccine_name", vaccineName);
        if (weeksFromBirth != null) {
            values.put("weeks_from_birth", weeksFromBirth);
        }
        if (dateAdministered != null) {
            values.put("date_administered", dateAdministered);
        }
        if (status != null) {
            values.put("status", status);
        }
        if (notes != null) {
            values.put("notes", notes);
        }
        return db.insert("vaccines", null, values);
    }

    public List<Map<String, Object>> getVaccinesByGuardianId(long guardianId) {
        List<Map<String, Object>> vaccines = new ArrayList<>();
        Cursor cursor = db.query("vaccines", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "weeks_from_birth ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> vaccine = new HashMap<>();
                vaccine.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                vaccine.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                vaccine.put("vaccine_name", cursor.getString(cursor.getColumnIndexOrThrow("vaccine_name")));
                int weeksIndex = cursor.getColumnIndex("weeks_from_birth");
                if (weeksIndex >= 0 && !cursor.isNull(weeksIndex)) {
                    vaccine.put("weeks_from_birth", cursor.getInt(weeksIndex));
                }
                int dateIndex = cursor.getColumnIndex("date_administered");
                if (dateIndex >= 0 && !cursor.isNull(dateIndex)) {
                    vaccine.put("date_administered", cursor.getString(dateIndex));
                }
                int statusIndex = cursor.getColumnIndex("status");
                if (statusIndex >= 0 && !cursor.isNull(statusIndex)) {
                    vaccine.put("status", cursor.getString(statusIndex));
                }
                int notesIndex = cursor.getColumnIndex("notes");
                if (notesIndex >= 0 && !cursor.isNull(notesIndex)) {
                    vaccine.put("notes", cursor.getString(notesIndex));
                }
                vaccines.add(vaccine);
            }
            cursor.close();
        }
        return vaccines;
    }

    public List<Map<String, Object>> getAllStandardVaccinations() {
        List<Map<String, Object>> vaccinations = new ArrayList<>();
        Cursor cursor = db.query("standardvaccinations", null, null, null, null, null, "weeks_from_birth ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> vaccination = new HashMap<>();
                vaccination.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                vaccination.put("vaccine_name", cursor.getString(cursor.getColumnIndexOrThrow("vaccine_name")));
                vaccination.put("weeks_from_birth", cursor.getInt(cursor.getColumnIndexOrThrow("weeks_from_birth")));
                int typeIndex = cursor.getColumnIndex("vaccine_type");
                if (typeIndex >= 0 && !cursor.isNull(typeIndex)) {
                    vaccination.put("vaccine_type", cursor.getString(typeIndex));
                }
                int descIndex = cursor.getColumnIndex("description");
                if (descIndex >= 0 && !cursor.isNull(descIndex)) {
                    vaccination.put("description", cursor.getString(descIndex));
                }
                vaccinations.add(vaccination);
            }
            cursor.close();
        }
        return vaccinations;
    }

    public void updateVaccine(long vaccineId, String dateAdministered, String status, String notes) {
        ContentValues values = new ContentValues();
        if (dateAdministered != null) {
            values.put("date_administered", dateAdministered);
        }
        if (status != null) {
            values.put("status", status);
        }
        if (notes != null) {
            values.put("notes", notes);
        }
        db.update("vaccines", values, "id = ?", new String[]{String.valueOf(vaccineId)});
    }
}



