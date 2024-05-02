package com.example.tueankinya.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profile.sqlite";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProfileTableQuery = "CREATE TABLE profile_table (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, sickness TEXT, hospital TEXT)";
        db.execSQL(createProfileTableQuery);
        String createAppointmentTableQuery = "CREATE TABLE appointment_table ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "appointment_date TEXT NOT NULL, "
                + "hospitalappointment TEXT NOT NULL);";
        db.execSQL(createAppointmentTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
        }
    }
}