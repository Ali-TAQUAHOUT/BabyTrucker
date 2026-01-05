package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDao {
    private SQLiteDatabase db;

    public ChatDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertMessage(long guardianId, long senderId, String senderRole, String message) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("sender_id", senderId);
        values.put("sender_role", senderRole);
        values.put("message", message);
        return db.insert("chat_messages", null, values);
    }

    public List<Map<String, Object>> getMessagesByGuardianId(long guardianId) {
        List<Map<String, Object>> messages = new ArrayList<>();
        Cursor cursor = db.query("chat_messages", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "created_at ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> message = new HashMap<>();
                message.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                message.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                message.put("sender_id", cursor.getLong(cursor.getColumnIndexOrThrow("sender_id")));
                message.put("sender_role", cursor.getString(cursor.getColumnIndexOrThrow("sender_role")));
                message.put("message", cursor.getString(cursor.getColumnIndexOrThrow("message")));
                message.put("created_at", cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }
}



