package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineDao {
    private SQLiteDatabase db;

    public MedicineDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertMedicine(long guardianId, String medicineName, String dosage, 
                              String frequency, String startDate, String endDate, 
                              String notes, String prescribedBy) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("medicine_name", medicineName);
        if (dosage != null) {
            values.put("dosage", dosage);
        }
        if (frequency != null) {
            values.put("frequency", frequency);
        }
        if (startDate != null) {
            values.put("start_date", startDate);
        }
        if (endDate != null) {
            values.put("end_date", endDate);
        }
        if (notes != null) {
            values.put("notes", notes);
        }
        if (prescribedBy != null) {
            values.put("prescribed_by", prescribedBy);
        }
        return db.insert("medicines", null, values);
    }

    public List<Map<String, Object>> getMedicinesByGuardianId(long guardianId) {
        List<Map<String, Object>> medicines = new ArrayList<>();
        Cursor cursor = db.query("medicines", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "start_date DESC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> medicine = new HashMap<>();
                medicine.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                medicine.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                medicine.put("medicine_name", cursor.getString(cursor.getColumnIndexOrThrow("medicine_name")));
                int dosageIndex = cursor.getColumnIndex("dosage");
                if (dosageIndex >= 0 && !cursor.isNull(dosageIndex)) {
                    medicine.put("dosage", cursor.getString(dosageIndex));
                }
                int frequencyIndex = cursor.getColumnIndex("frequency");
                if (frequencyIndex >= 0 && !cursor.isNull(frequencyIndex)) {
                    medicine.put("frequency", cursor.getString(frequencyIndex));
                }
                int startDateIndex = cursor.getColumnIndex("start_date");
                if (startDateIndex >= 0 && !cursor.isNull(startDateIndex)) {
                    medicine.put("start_date", cursor.getString(startDateIndex));
                }
                int endDateIndex = cursor.getColumnIndex("end_date");
                if (endDateIndex >= 0 && !cursor.isNull(endDateIndex)) {
                    medicine.put("end_date", cursor.getString(endDateIndex));
                }
                int notesIndex = cursor.getColumnIndex("notes");
                if (notesIndex >= 0 && !cursor.isNull(notesIndex)) {
                    medicine.put("notes", cursor.getString(notesIndex));
                }
                int prescribedByIndex = cursor.getColumnIndex("prescribed_by");
                if (prescribedByIndex >= 0 && !cursor.isNull(prescribedByIndex)) {
                    medicine.put("prescribed_by", cursor.getString(prescribedByIndex));
                }
                medicines.add(medicine);
            }
            cursor.close();
        }
        return medicines;
    }

    public void deleteMedicine(long medicineId) {
        db.delete("medicines", "id = ?", new String[]{String.valueOf(medicineId)});
    }
}



