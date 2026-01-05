package com.example.babyone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.babyone.database.BabyDatabase;
import com.example.babyone.database.dao.UserDao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AuthHelper {
    private static final String PREFS_NAME = "BabyAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String TAG = "AuthHelper";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }

    public static boolean registerUser(Context context, String username, String password, 
                                      String email, String role, String name, String mobile) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        UserDao userDao = new UserDao(db);

        // Check if username already exists
        if (userDao.usernameExists(username)) {
            db.close();
            return false;
        }

        // Hash password
        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            db.close();
            return false;
        }

        // Insert user
        long userId = userDao.insertUser(username, passwordHash, email, role, name, mobile);
        db.close();

        return userId > 0;
    }

    public static Map<String, Object> login(Context context, String username, String password) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserDao userDao = new UserDao(db);

        // Get user by username
        Map<String, Object> user = userDao.getUserByUsername(username);
        if (user == null) {
            db.close();
            return null;
        }

        // Verify password
        String storedHash = (String) user.get("password_hash");
        String inputHash = hashPassword(password);

        if (inputHash == null || !storedHash.equals(inputHash)) {
            db.close();
            return null;
        }

        // Save session
        saveSession(context, (Long) user.get("id"), username, (String) user.get("role"));

        db.close();
        return user;
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_ROLE);
        editor.apply();
    }

    public static void saveSession(Context context, long userId, String username, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public static Map<String, Object> getCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long userId = prefs.getLong(KEY_USER_ID, -1);
        
        if (userId == -1) {
            return null;
        }

        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserDao userDao = new UserDao(db);
        Map<String, Object> user = userDao.getUserById(userId);
        db.close();

        return user;
    }

    public static long getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public static String getCurrentUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, null);
    }

    public static String getCurrentUserRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE, null);
    }
}



