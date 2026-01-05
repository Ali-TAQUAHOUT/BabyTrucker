package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordDao {
    private SQLiteDatabase db;

    public RecordDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertWeightRecord(long guardianId, double weight, String dateRecorded) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("weight", weight);
        values.put("date_recorded", dateRecorded);
        return db.insert("weight_records", null, values);
    }

    public long insertHeightRecord(long guardianId, double height, String dateRecorded) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("height", height);
        values.put("date_recorded", dateRecorded);
        return db.insert("height_records", null, values);
    }

    public List<Map<String, Object>> getWeightRecordsByGuardianId(long guardianId) {
        List<Map<String, Object>> records = new ArrayList<>();
        Cursor cursor = db.query("weight_records", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "date_recorded ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                record.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                record.put("weight", cursor.getDouble(cursor.getColumnIndexOrThrow("weight")));
                record.put("date_recorded", cursor.getString(cursor.getColumnIndexOrThrow("date_recorded")));
                records.add(record);
            }
            cursor.close();
        }
        return records;
    }

    public List<Map<String, Object>> getHeightRecordsByGuardianId(long guardianId) {
        List<Map<String, Object>> records = new ArrayList<>();
        Cursor cursor = db.query("height_records", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "date_recorded ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                record.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                record.put("height", cursor.getDouble(cursor.getColumnIndexOrThrow("height")));
                record.put("date_recorded", cursor.getString(cursor.getColumnIndexOrThrow("date_recorded")));
                records.add(record);
            }
            cursor.close();
        }
        return records;
    }
}



