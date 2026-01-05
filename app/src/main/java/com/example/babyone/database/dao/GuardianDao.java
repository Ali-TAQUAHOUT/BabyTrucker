package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuardianDao {
    private SQLiteDatabase db;

    public GuardianDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertGuardian(long userId, String babyName, String babyBday, String babyGender, 
                              Double currentWeight, Double currentHeight) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("babyname", babyName);
        values.put("baby_bday", babyBday);
        values.put("baby_gender", babyGender);
        if (currentWeight != null) {
            values.put("current_weight", currentWeight);
        }
        if (currentHeight != null) {
            values.put("current_height", currentHeight);
        }
        return db.insert("guardians", null, values);
    }

    public Map<String, Object> getGuardianByUserId(long userId) {
        Cursor cursor = db.query("guardians", null, "user_id = ?", 
                new String[]{String.valueOf(userId)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Map<String, Object> guardian = new HashMap<>();
            guardian.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            guardian.put("user_id", cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            guardian.put("babyname", cursor.getString(cursor.getColumnIndexOrThrow("babyname")));
            guardian.put("baby_bday", cursor.getString(cursor.getColumnIndexOrThrow("baby_bday")));
            guardian.put("baby_gender", cursor.getString(cursor.getColumnIndexOrThrow("baby_gender")));
            int weightIndex = cursor.getColumnIndex("current_weight");
            if (weightIndex >= 0 && !cursor.isNull(weightIndex)) {
                guardian.put("current_weight", cursor.getDouble(weightIndex));
            }
            int heightIndex = cursor.getColumnIndex("current_height");
            if (heightIndex >= 0 && !cursor.isNull(heightIndex)) {
                guardian.put("current_height", cursor.getDouble(heightIndex));
            }
            cursor.close();
            return guardian;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public List<Map<String, Object>> searchGuardiansByName(String searchTerm) {
        List<Map<String, Object>> results = new ArrayList<>();
        Cursor cursor = db.query("guardians", null, "babyname LIKE ?", 
                new String[]{"%" + searchTerm + "%"}, null, null, null);
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> guardian = new HashMap<>();
                guardian.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                guardian.put("user_id", cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
                guardian.put("babyname", cursor.getString(cursor.getColumnIndexOrThrow("babyname")));
                guardian.put("baby_bday", cursor.getString(cursor.getColumnIndexOrThrow("baby_bday")));
                guardian.put("baby_gender", cursor.getString(cursor.getColumnIndexOrThrow("baby_gender")));
                results.add(guardian);
            }
            cursor.close();
        }
        return results;
    }

    public void updateGuardian(long guardianId, String babyName, String babyBday, String babyGender, 
                               Double currentWeight, Double currentHeight) {
        ContentValues values = new ContentValues();
        if (babyName != null) values.put("babyname", babyName);
        if (babyBday != null) values.put("baby_bday", babyBday);
        if (babyGender != null) values.put("baby_gender", babyGender);
        if (currentWeight != null) values.put("current_weight", currentWeight);
        if (currentHeight != null) values.put("current_height", currentHeight);
        
        db.update("guardians", values, "id = ?", new String[]{String.valueOf(guardianId)});
    }
}



