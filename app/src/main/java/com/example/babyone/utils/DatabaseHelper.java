package com.example.babyone.utils;

import android.content.Context;
import android.util.Log;

import com.example.babyone.database.BabyDatabase;
import com.example.babyone.database.dao.GuardianDao;
import com.example.babyone.database.dao.MedicineDao;
import com.example.babyone.database.dao.RecordDao;
import com.example.babyone.database.dao.UserDao;
import com.example.babyone.database.dao.VaccineDao;

import java.util.List;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    // Interface compatible with FirestoreHelper
    public interface DataCallback {
        void onDataLoaded(HashMap<String, Map<String, Object>> dataMap);
    }

    public static void addToCollection(String collectionName, HashMap<String, Object> data, Context context) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = AuthHelper.getCurrentUserId(context);

        try {
            if (collectionName.equals("guardians")) {
                GuardianDao guardianDao = new GuardianDao(db);
                String babyName = (String) data.get("babyname");
                String babyBday = (String) data.get("baby_bday");
                String babyGender = (String) data.get("baby_gender");
                Double weight = data.get("current_weight") != null ? 
                    Double.parseDouble(data.get("current_weight").toString()) : null;
                Double height = data.get("current_height") != null ? 
                    Double.parseDouble(data.get("current_height").toString()) : null;
                
                long guardianId = guardianDao.insertGuardian(userId, babyName, babyBday, babyGender, weight, height);
                
                // Calculate and store vaccine data
                if (guardianId > 0) {
                    VaccineDao vaccineDao = new VaccineDao(db);
                    List<Map<String, Object>> standardVaccines = vaccineDao.getAllStandardVaccinations();
                    for (Map<String, Object> vaccine : standardVaccines) {
                        vaccineDao.insertVaccine(guardianId, 
                            (String) vaccine.get("vaccine_name"),
                            (Integer) vaccine.get("weeks_from_birth"),
                            null, "pending", null);
                    }
                }
            } else if (collectionName.equals("users")) {
                // Role update handled separately
            } else if (collectionName.equals("DoctorLog")) {
                UserDao userDao = new UserDao(db);
                String name = (String) data.get("name");
                String mobile = (String) data.get("mobile");
                // Update user with doctor info
                android.content.ContentValues values = new android.content.ContentValues();
                values.put("name", name);
                values.put("mobile", mobile);
                db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
                
                // Insert into DoctorLog
                values = new android.content.ContentValues();
                values.put("user_id", userId);
                values.put("name", name);
                values.put("mobile", mobile);
                db.insert("DoctorLog", null, values);
            } else if (collectionName.equals("NannyLog") || collectionName.equals("MidWifeLog")) {
                UserDao userDao = new UserDao(db);
                String name = (String) data.get("name");
                String mobile = (String) data.get("mobile");
                // Update user with nanny info
                android.content.ContentValues values = new android.content.ContentValues();
                values.put("name", name);
                values.put("mobile", mobile);
                db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
                
                // Insert into NannyLog
                values = new android.content.ContentValues();
                values.put("user_id", userId);
                values.put("name", name);
                values.put("mobile", mobile);
                db.insert("NannyLog", null, values);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding to collection: " + collectionName, e);
        } finally {
            db.close();
        }
    }

    public static void readFromCollection(String collectionName, String identifier, DataCallback callback) {
        // This method mimics FirestoreHelper.readFromCollection
        // identifier can be email (for guardians) or username
        Context context = null; // Will need to pass context
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        HashMap<String, Map<String, Object>> dataMap = new HashMap<>();

        try {
            if (collectionName.equals("guardians")) {
                // Get user by email/username, then get guardian
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    long userId = (Long) user.get("id");
                    GuardianDao guardianDao = new GuardianDao(db);
                    Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
                    if (guardian != null) {
                        dataMap.put(String.valueOf(guardian.get("id")), guardian);
                    }
                }
            } else if (collectionName.equals("users")) {
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    dataMap.put(String.valueOf(user.get("id")), user);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading from collection: " + collectionName, e);
        } finally {
            db.close();
            if (callback != null) {
                callback.onDataLoaded(dataMap);
            }
        }
    }

    public static void readFromCollection(Context context, String collectionName, String identifier, DataCallback callback) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        HashMap<String, Map<String, Object>> dataMap = new HashMap<>();

        try {
            if (collectionName.equals("guardians") || collectionName.equals("guardians/")) {
                // Get user by username, then get guardian
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    long userId = (Long) user.get("id");
                    GuardianDao guardianDao = new GuardianDao(db);
                    Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
                    if (guardian != null) {
                        dataMap.put(String.valueOf(guardian.get("id")), guardian);
                    }
                }
            } else if (collectionName.equals("users")) {
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    dataMap.put(String.valueOf(user.get("id")), user);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading from collection: " + collectionName, e);
        } finally {
            db.close();
            if (callback != null) {
                callback.onDataLoaded(dataMap);
            }
        }
    }

    public static void readFromSubcollection(Context context, String collectionName, String identifier, 
                                            String subcollectionName, DataCallback callback) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        HashMap<String, Map<String, Object>> dataMap = new HashMap<>();

        try {
            if (collectionName.equals("guardians") && subcollectionName.equals("vaccines")) {
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    long userId = (Long) user.get("id");
                    GuardianDao guardianDao = new GuardianDao(db);
                    Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
                    if (guardian != null) {
                        long guardianId = (Long) guardian.get("id");
                        VaccineDao vaccineDao = new VaccineDao(db);
                        List<Map<String, Object>> vaccines = vaccineDao.getVaccinesByGuardianId(guardianId);
                        for (Map<String, Object> vaccine : vaccines) {
                            dataMap.put(String.valueOf(vaccine.get("id")), vaccine);
                        }
                    }
                }
            } else if (collectionName.equals("guardians") && subcollectionName.equals("medicines")) {
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserByUsername(identifier);
                if (user != null) {
                    long userId = (Long) user.get("id");
                    GuardianDao guardianDao = new GuardianDao(db);
                    Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
                    if (guardian != null) {
                        long guardianId = (Long) guardian.get("id");
                        MedicineDao medicineDao = new MedicineDao(db);
                        List<Map<String, Object>> medicines = medicineDao.getMedicinesByGuardianId(guardianId);
                        for (Map<String, Object> medicine : medicines) {
                            dataMap.put(String.valueOf(medicine.get("id")), medicine);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading from subcollection: " + subcollectionName, e);
        } finally {
            db.close();
            if (callback != null) {
                callback.onDataLoaded(dataMap);
            }
        }
    }

    public static List<Map<String, Object>> getAllGuardians(Context context) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Map<String, Object>> guardians = new java.util.ArrayList<>();
        
        try {
            GuardianDao guardianDao = new GuardianDao(db);
            android.database.Cursor cursor = db.query("guardians", null, null, null, null, null, null);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Map<String, Object> guardian = new HashMap<>();
                    guardian.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                    guardian.put("user_id", cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
                    guardian.put("babyname", cursor.getString(cursor.getColumnIndexOrThrow("babyname")));
                    guardian.put("baby_bday", cursor.getString(cursor.getColumnIndexOrThrow("baby_bday")));
                    guardian.put("baby_gender", cursor.getString(cursor.getColumnIndexOrThrow("baby_gender")));
                    
                    // Get user info for parent name and username
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow("user_id"));
                    UserDao userDao = new UserDao(db);
                    Map<String, Object> user = userDao.getUserById(userId);
                    if (user != null) {
                        guardian.put("parentname", user.get("name"));
                        guardian.put("email", user.get("username")); // Use username instead of email for compatibility
                    }
                    
                    guardians.add(guardian);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all guardians", e);
        } finally {
            db.close();
        }
        return guardians;
    }

    public static List<Map<String, Object>> searchGuardiansByName(Context context, String searchTerm) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Map<String, Object>> guardians = new java.util.ArrayList<>();
        
        try {
            GuardianDao guardianDao = new GuardianDao(db);
            List<Map<String, Object>> results = guardianDao.searchGuardiansByName(searchTerm);
            
            for (Map<String, Object> guardian : results) {
                long userId = (Long) guardian.get("user_id");
                UserDao userDao = new UserDao(db);
                Map<String, Object> user = userDao.getUserById(userId);
                if (user != null) {
                    guardian.put("parentname", user.get("name"));
                    guardian.put("email", user.get("username")); // Use username instead of email for compatibility
                }
                guardians.add(guardian);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching guardians", e);
        } finally {
            db.close();
        }
        return guardians;
    }

    public static Map<String, Object> getDoctorInfo(Context context, String username) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, Object> doctorInfo = null;
        
        try {
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user != null) {
                long userId = (Long) user.get("id");
                android.database.Cursor cursor = db.query("DoctorLog", null, "user_id = ?", 
                        new String[]{String.valueOf(userId)}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    doctorInfo = new HashMap<>();
                    doctorInfo.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    doctorInfo.put("mobile", cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting doctor info", e);
        } finally {
            db.close();
        }
        return doctorInfo;
    }

    public static Map<String, Object> getNannyInfo(Context context, String username) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, Object> nannyInfo = null;
        
        try {
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user != null) {
                long userId = (Long) user.get("id");
                android.database.Cursor cursor = db.query("NannyLog", null, "user_id = ?", 
                        new String[]{String.valueOf(userId)}, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    // Fallback to old table name for migration
                    cursor = db.query("MidWifeLog", null, "user_id = ?", 
                            new String[]{String.valueOf(userId)}, null, null, null);
                }
                if (cursor != null && cursor.moveToFirst()) {
                    nannyInfo = new HashMap<>();
                    nannyInfo.put("name", cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    nannyInfo.put("mobile", cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting nanny info", e);
        } finally {
            db.close();
        }
        return nannyInfo;
    }

    // Keep old method name for backward compatibility
    public static Map<String, Object> getMidWifeInfo(Context context, String username) {
        return getNannyInfo(context, username);
    }

    public static boolean updateBabyWeightAndHeight(Context context, String username, double weight, double height, String date) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Get user by username
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user == null) {
                return false;
            }
            
            long userId = (Long) user.get("id");
            GuardianDao guardianDao = new GuardianDao(db);
            Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
            if (guardian == null) {
                return false;
            }
            
            long guardianId = (Long) guardian.get("id");
            
            // Update current weight and height in guardians table
            guardianDao.updateGuardian(guardianId, null, null, null, weight, height);
            
            // Add records to weight_records and height_records
            RecordDao recordDao = new RecordDao(db);
            recordDao.insertWeightRecord(guardianId, weight, date);
            recordDao.insertHeightRecord(guardianId, height, date);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error updating weight and height", e);
            return false;
        } finally {
            db.close();
        }
    }

    public static boolean addVaccine(Context context, String username, String vaccineName, String dateAdministered, String place) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Get user by username
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user == null) {
                return false;
            }
            
            long userId = (Long) user.get("id");
            GuardianDao guardianDao = new GuardianDao(db);
            Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
            if (guardian == null) {
                return false;
            }
            
            long guardianId = (Long) guardian.get("id");
            
            // Find the vaccine by name and update it
            VaccineDao vaccineDao = new VaccineDao(db);
            List<Map<String, Object>> vaccines = vaccineDao.getVaccinesByGuardianId(guardianId);
            
            boolean found = false;
            for (Map<String, Object> vaccine : vaccines) {
                String vName = (String) vaccine.get("vaccine_name");
                if (vName != null && vName.equalsIgnoreCase(vaccineName)) {
                    long vaccineId = (Long) vaccine.get("id");
                    vaccineDao.updateVaccine(vaccineId, dateAdministered, "completed", place);
                    found = true;
                    break;
                }
            }
            
            // If vaccine not found, create a new one
            if (!found) {
                vaccineDao.insertVaccine(guardianId, vaccineName, null, dateAdministered, "completed", place);
            }
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding vaccine", e);
            return false;
        } finally {
            db.close();
        }
    }

    public static boolean addMedicine(Context context, String username, String medicineName, String givenDate, String place) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        try {
            // Get user by username
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user == null) {
                return false;
            }
            
            long userId = (Long) user.get("id");
            GuardianDao guardianDao = new GuardianDao(db);
            Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
            if (guardian == null) {
                return false;
            }
            
            long guardianId = (Long) guardian.get("id");
            
            // Add medicine
            MedicineDao medicineDao = new MedicineDao(db);
            String prescribedBy = AuthHelper.getCurrentUsername(context);
            medicineDao.insertMedicine(guardianId, medicineName, null, null, givenDate, null, place, prescribedBy);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding medicine", e);
            return false;
        } finally {
            db.close();
        }
    }

    public static long getGuardianIdByUsername(Context context, String username) {
        BabyDatabase dbHelper = new BabyDatabase(context);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            UserDao userDao = new UserDao(db);
            Map<String, Object> user = userDao.getUserByUsername(username);
            if (user != null) {
                long userId = (Long) user.get("id");
                GuardianDao guardianDao = new GuardianDao(db);
                Map<String, Object> guardian = guardianDao.getGuardianByUserId(userId);
                if (guardian != null) {
                    return (Long) guardian.get("id");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting guardian ID", e);
        } finally {
            db.close();
        }
        return -1;
    }
}

