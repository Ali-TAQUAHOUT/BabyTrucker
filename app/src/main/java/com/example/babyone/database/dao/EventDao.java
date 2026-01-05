package com.example.babyone.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDao {
    private SQLiteDatabase db;

    public EventDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertEvent(long guardianId, String eventName, String eventDate, 
                           String eventType, String description, String createdBy) {
        ContentValues values = new ContentValues();
        values.put("guardian_id", guardianId);
        values.put("event_name", eventName);
        values.put("event_date", eventDate);
        if (eventType != null) {
            values.put("event_type", eventType);
        }
        if (description != null) {
            values.put("description", description);
        }
        if (createdBy != null) {
            values.put("created_by", createdBy);
        }
        return db.insert("events", null, values);
    }

    public List<Map<String, Object>> getEventsByGuardianId(long guardianId) {
        List<Map<String, Object>> events = new ArrayList<>();
        Cursor cursor = db.query("events", null, "guardian_id = ?", 
                new String[]{String.valueOf(guardianId)}, null, null, "event_date ASC");
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> event = new HashMap<>();
                event.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                event.put("guardian_id", cursor.getLong(cursor.getColumnIndexOrThrow("guardian_id")));
                event.put("event_name", cursor.getString(cursor.getColumnIndexOrThrow("event_name")));
                event.put("event_date", cursor.getString(cursor.getColumnIndexOrThrow("event_date")));
                int typeIndex = cursor.getColumnIndex("event_type");
                if (typeIndex >= 0 && !cursor.isNull(typeIndex)) {
                    event.put("event_type", cursor.getString(typeIndex));
                }
                int descIndex = cursor.getColumnIndex("description");
                if (descIndex >= 0 && !cursor.isNull(descIndex)) {
                    event.put("description", cursor.getString(descIndex));
                }
                int createdByIndex = cursor.getColumnIndex("created_by");
                if (createdByIndex >= 0 && !cursor.isNull(createdByIndex)) {
                    event.put("created_by", cursor.getString(createdByIndex));
                }
                events.add(event);
            }
            cursor.close();
        }
        return events;
    }

    public void updateEvent(long eventId, String eventName, String eventDate, 
                           String eventType, String description) {
        ContentValues values = new ContentValues();
        if (eventName != null) {
            values.put("event_name", eventName);
        }
        if (eventDate != null) {
            values.put("event_date", eventDate);
        }
        if (eventType != null) {
            values.put("event_type", eventType);
        }
        if (description != null) {
            values.put("description", description);
        }
        db.update("events", values, "id = ?", new String[]{String.valueOf(eventId)});
    }

    public void deleteEvent(long eventId) {
        db.delete("events", "id = ?", new String[]{String.valueOf(eventId)});
    }
}



