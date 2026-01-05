package com.example.babyone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BabyDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "baby_tracking.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "BabyDatabase";

    // Table Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "password_hash TEXT NOT NULL, " +
            "email TEXT, " +
            "role TEXT NOT NULL, " +
            "name TEXT, " +
            "mobile TEXT, " +
            "created_at INTEGER DEFAULT (strftime('%s', 'now'))" +
            ")";

    // Table Guardians
    private static final String CREATE_TABLE_GUARDIANS = "CREATE TABLE guardians (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "babyname TEXT NOT NULL, " +
            "baby_bday TEXT NOT NULL, " +
            "baby_gender TEXT NOT NULL, " +
            "current_weight REAL, " +
            "current_height REAL, " +
            "created_at INTEGER DEFAULT (strftime('%s', 'now')), " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";

    // Table Standard Vaccinations
    private static final String CREATE_TABLE_STANDARD_VACCINATIONS = "CREATE TABLE standardvaccinations (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "vaccine_name TEXT NOT NULL, " +
            "weeks_from_birth INTEGER NOT NULL, " +
            "vaccine_type TEXT, " +
            "description TEXT" +
            ")";

    // Table Vaccines (individual baby vaccines)
    private static final String CREATE_TABLE_VACCINES = "CREATE TABLE vaccines (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "vaccine_name TEXT NOT NULL, " +
            "weeks_from_birth INTEGER, " +
            "date_administered TEXT, " +
            "status TEXT, " +
            "notes TEXT, " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE" +
            ")";

    // Table Medicines
    private static final String CREATE_TABLE_MEDICINES = "CREATE TABLE medicines (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "medicine_name TEXT NOT NULL, " +
            "dosage TEXT, " +
            "frequency TEXT, " +
            "start_date TEXT, " +
            "end_date TEXT, " +
            "notes TEXT, " +
            "prescribed_by TEXT, " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE" +
            ")";

    // Table Weight Records
    private static final String CREATE_TABLE_WEIGHT_RECORDS = "CREATE TABLE weight_records (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "weight REAL NOT NULL, " +
            "date_recorded TEXT NOT NULL, " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE" +
            ")";

    // Table Height Records
    private static final String CREATE_TABLE_HEIGHT_RECORDS = "CREATE TABLE height_records (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "height REAL NOT NULL, " +
            "date_recorded TEXT NOT NULL, " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE" +
            ")";

    // Table Notifications
    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE notifications (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "title TEXT NOT NULL, " +
            "message TEXT NOT NULL, " +
            "type TEXT, " +
            "read_status INTEGER DEFAULT 0, " +
            "created_at INTEGER DEFAULT (strftime('%s', 'now')), " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";

    // Table DoctorLog
    private static final String CREATE_TABLE_DOCTOR_LOG = "CREATE TABLE DoctorLog (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "name TEXT, " +
            "mobile TEXT, " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";

    // Table NannyLog
    private static final String CREATE_TABLE_NANNY_LOG = "CREATE TABLE NannyLog (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "name TEXT, " +
            "mobile TEXT, " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";

    // Table Events (custom events that can be added by doctors/midwives)
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE events (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "event_name TEXT NOT NULL, " +
            "event_date TEXT NOT NULL, " +
            "event_type TEXT, " +
            "description TEXT, " +
            "created_by TEXT, " +
            "created_at INTEGER DEFAULT (strftime('%s', 'now')), " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE" +
            ")";

    // Table Chat Messages (between parent and doctor)
    private static final String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE chat_messages (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "guardian_id INTEGER NOT NULL, " +
            "sender_id INTEGER NOT NULL, " +
            "sender_role TEXT NOT NULL, " +
            "message TEXT NOT NULL, " +
            "created_at INTEGER DEFAULT (strftime('%s', 'now')), " +
            "FOREIGN KEY (guardian_id) REFERENCES guardians(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")";

    public BabyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GUARDIANS);
        db.execSQL(CREATE_TABLE_STANDARD_VACCINATIONS);
        db.execSQL(CREATE_TABLE_VACCINES);
        db.execSQL(CREATE_TABLE_MEDICINES);
        db.execSQL(CREATE_TABLE_WEIGHT_RECORDS);
        db.execSQL(CREATE_TABLE_HEIGHT_RECORDS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(CREATE_TABLE_DOCTOR_LOG);
        db.execSQL(CREATE_TABLE_NANNY_LOG);
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        
        // Initialize standard vaccinations
        initializeStandardVaccinations(db);
        
        Log.d(TAG, "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            // Add new tables for events and chat
            db.execSQL(CREATE_TABLE_EVENTS);
            db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        }
    }

    private void initializeStandardVaccinations(SQLiteDatabase db) {
        // Government vaccines
        String[] govVaccines = {
            "BCG", "OPV", "DPT", "HepB", "MMR", "Tetanus"
        };
        int[] govWeeks = {0, 6, 10, 14, 18, 24};
        
        // Private vaccines
        String[] privateVaccines = {
            "Rotavirus", "Pneumococcal", "Hib", "Varicella"
        };
        int[] privateWeeks = {6, 8, 12, 15};
        
        for (int i = 0; i < govVaccines.length; i++) {
            db.execSQL("INSERT INTO standardvaccinations (vaccine_name, weeks_from_birth, vaccine_type, description) VALUES (?, ?, ?, ?)",
                    new Object[]{govVaccines[i], govWeeks[i], "government", "Standard government vaccination"});
        }
        
        for (int i = 0; i < privateVaccines.length; i++) {
            db.execSQL("INSERT INTO standardvaccinations (vaccine_name, weeks_from_birth, vaccine_type, description) VALUES (?, ?, ?, ?)",
                    new Object[]{privateVaccines[i], privateWeeks[i], "private", "Standard private vaccination"});
        }
        
    }
}

