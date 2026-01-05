package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserDao {
    private SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertUser(String username, String passwordHash, String email, String role, String name, String mobile) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password_hash", passwordHash);
        values.put("email", email);
        values.put("role", role);
        values.put("name", name);
        values.put("mobile", mobile);
        return db.insert("users", null, values);
    }

    public Map<String, Object> getUserByUsername(String username) {
        Cursor cursor = db.query("users", null, "username = ?", 
                new String[]{username}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            user.put("username", cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.put("password_hash", cursor.getString(cursor.getColumnIndexOrThrow("password_hash")));
            user.put("email", cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.put("role", cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.put("mobile", cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public Map<String, Object> getUserById(long userId) {
        Cursor cursor = db.query("users", null, "id = ?", 
                new String[]{String.valueOf(userId)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            user.put("username", cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.put("email", cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.put("role", cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.put("mobile", cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        Cursor cursor = db.query("users", new String[]{"id"}, "username = ?", 
                new String[]{username}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
}



