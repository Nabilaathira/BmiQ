package com.example.bmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.OpenForTesting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Db.Bmi";

    public static class UserEntry {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_LOGGED_IN = "logged_in";
    }

    public static class UserBmi {
        public static final String TABLE_NAME = "UserBmi";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_BMI = "bmi";
        public static final String COLUMN_JUDUL = "judul";
        public static final String COLUMN_BERAT = "berat";
        public static final String COLUMN_TINGGI = "tinggi";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_WEIGHT_CATEGORY = "weight_category";

    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    UserEntry.COLUMN_NAME_LOGGED_IN + " INTEGER DEFAULT 0)";

    private static final String SQL_CREATE_USER_BMI =
            "CREATE TABLE " + UserBmi.TABLE_NAME + " (" +
                    UserBmi.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserBmi.COLUMN_JUDUL + " TEXT," +
                    UserBmi.COLUMN_BERAT + " REAL," +
                    UserBmi.COLUMN_TINGGI + " REAL," +
                    UserBmi.COLUMN_BMI + " REAL," +
                    UserBmi.COLUMN_WEIGHT_CATEGORY + " TEXT," +
                    UserBmi.COLUMN_CREATED_AT + " TEXT," +
                    UserBmi.COLUMN_UPDATED_AT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    private static final String SQL_DELETE_USER_BMI =
            "DROP TABLE IF EXISTS " + UserBmi.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_USER_BMI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_USER_BMI);
        onCreate(db);
    }

    public void insertData(String judul, float berat, float tinggi, float bmi, String weightCategory) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserBmi.COLUMN_JUDUL, judul);
        values.put(UserBmi.COLUMN_BERAT, berat);
        values.put(UserBmi.COLUMN_TINGGI, tinggi);
        values.put(UserBmi.COLUMN_BMI, bmi);
        values.put(UserBmi.COLUMN_WEIGHT_CATEGORY, weightCategory);
        String currentTime = getCurrentDateTime();
        values.put(UserBmi.COLUMN_CREATED_AT, currentTime);
        values.put(UserBmi.COLUMN_UPDATED_AT, currentTime);
        db.insert(UserBmi.TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(UserBmi.TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor searchByTitle(String judul) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + UserBmi.TABLE_NAME + " WHERE " + UserBmi.COLUMN_JUDUL + " LIKE ?", new String[]{"%" + judul + "%"});
    }

    public void updateRecord(int id, String judul, float berat, float tinggi, float bmi, String weightCategory) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserBmi.COLUMN_JUDUL, judul);
        values.put(UserBmi.COLUMN_BERAT, berat);
        values.put(UserBmi.COLUMN_TINGGI, tinggi);
        values.put(UserBmi.COLUMN_BMI, bmi); // Update nilai BMI
        values.put(UserBmi.COLUMN_WEIGHT_CATEGORY, weightCategory); // Update kategori berat badan
        values.put(UserBmi.COLUMN_UPDATED_AT, getCurrentDateTime());
        db.update(UserBmi.TABLE_NAME, values, UserBmi.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(UserBmi.TABLE_NAME, UserBmi.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public long registerUser(String user, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, user);
        values.put(UserEntry.COLUMN_NAME_PASSWORD, password);
        long id = db.insert(UserEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public boolean isValidLogin(String user, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_USERNAME + " = ? AND " + UserEntry.COLUMN_NAME_PASSWORD + " = ?", new String[]{user, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public void saveLoginStatus(String user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_LOGGED_IN, 1);
        db.update(UserEntry.TABLE_NAME, values, UserEntry.COLUMN_NAME_USERNAME + " = ?", new String[]{user});
        db.close();
    }

    public boolean checkLoginStatus() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isLoggedIn;
    }

    public String getLoggedInUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + UserEntry.COLUMN_NAME_USERNAME + " FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        if (cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME));
            cursor.close();
            db.close();
            return username;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public void logout() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_LOGGED_IN, 0);
        db.update(UserEntry.TABLE_NAME, values, null, null);
        db.close();
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME,
                null,
                UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public String getTableName() {
        return UserBmi.TABLE_NAME;
    }

    public String getColumnId() {
        return UserBmi.COLUMN_ID;
    }

    public String getColumnBmi() {
        return UserBmi.COLUMN_BMI;
    }

    public String getColumnJudul() {
        return UserBmi.COLUMN_JUDUL;
    }

    public String getColumnHeight() {
        return UserBmi.COLUMN_TINGGI;
    }

    public String getColumnWeight() {
        return UserBmi.COLUMN_BERAT;
    }

    public String getColumnCreatedAt() {
        return UserBmi.COLUMN_CREATED_AT;
    }

    public String getColumnUpdatedAt() {
        return UserBmi.COLUMN_UPDATED_AT;

    }public String getColumnWeightCategory() {
        return UserBmi.COLUMN_WEIGHT_CATEGORY;
    }
}
