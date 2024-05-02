package com.example.tueankinya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.model.TakingMedicine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DrugDatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "drug.sqlite";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_DRUG = "drug";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DRUG_NAME = "drug_name";
    public static final String COLUMN_AMOUNT_DRUG = "amount_drug";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_TIME_EAT = "time_eat";
    public static final String COLUMN_EAT_ACTIVE = "eat_active";
    public static final String COLUMN_BEFORE_TIME = "before_time";
    public static final String COLUMN_AFTER_TIME = "after_time";

    private static final String COLUMN_DELETED = "deleted";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_DRUG + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DRUG_NAME + " TEXT,"
            + COLUMN_AMOUNT_DRUG + " INTEGER,"
            + COLUMN_START_TIME + " TEXT,"
            + COLUMN_END_TIME + " TEXT,"
            + COLUMN_TIME_EAT + " TEXT,"
            + COLUMN_EAT_ACTIVE + " INTEGER,"
            + COLUMN_BEFORE_TIME + " INTEGER,"
            + COLUMN_AFTER_TIME + " INTEGER"
            + ")";

    public DrugDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRUG);
        onCreate(db);
    }

    public long insertDrug(DrugTime drugTimeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRUG_NAME, drugTimeModel.getDrugName());
        int amountDrug = 0;
        try {
            amountDrug = Integer.parseInt(drugTimeModel.getAmount_drug());
        } catch (NumberFormatException e) {
            amountDrug = 0;
        }
        values.put(COLUMN_AMOUNT_DRUG, amountDrug);
        values.put(COLUMN_START_TIME, drugTimeModel.getStartTime());
        values.put(COLUMN_END_TIME, drugTimeModel.getEndTime());
        values.put(COLUMN_TIME_EAT, drugTimeModel.getTimeEat());
        values.put(COLUMN_EAT_ACTIVE, drugTimeModel.getEatActive() ? 1 : 0);
        values.put(COLUMN_BEFORE_TIME, drugTimeModel.getBeforeTime() ? 1 : 0);
        values.put(COLUMN_AFTER_TIME, drugTimeModel.getAfterTime() ? 1 : 0);

        long rowId = db.insert(TABLE_DRUG, null, values);
        db.close();
        return rowId;
    }

    public void deleteDrug(long drugId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DRUG, COLUMN_ID + " = ?",
                new String[]{String.valueOf(drugId)});
        db.close();
    }

    public void deleteDrugByTime(String currentTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DRUG, COLUMN_TIME_EAT + " = ?", new String[]{currentTime});

        if (rowsAffected > 0) {
            Toast.makeText(context, "Drug(s) deleted successfully", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    public void setAllToDefault(long drugId) {
        boolean currentEatActive = getEatActiveForAnyDrug();

        if (currentEatActive) {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_EAT_ACTIVE, 0);
                db.update(TABLE_DRUG, values, null, null);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
            Toast.makeText(context, "All drugs set to default successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getEatActiveForDrug(long drugId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_EAT_ACTIVE + " FROM " + TABLE_DRUG +
                " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(drugId)});

        boolean eatActive = false;

        if (cursor.moveToFirst()) {
            eatActive = cursor.getInt(0) == 1;
        }

        cursor.close();
        db.close();

        return eatActive;
    }

    public void updateEatActive(long drugId, boolean eatActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EAT_ACTIVE, eatActive ? 1 : 0);

        db.update(TABLE_DRUG, values, COLUMN_ID + " = ?", new String[]{String.valueOf(drugId)});
        db.close();
    }

    public void setAllToDefault() {
        boolean currentEatActive = getEatActiveForAnyDrug();

        if (currentEatActive) {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_EAT_ACTIVE, 0);
                db.update(TABLE_DRUG, values, null, null);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
            Toast.makeText(context, "All drugs set to default successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getEatActiveForAnyDrug() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COLUMN_EAT_ACTIVE + ") FROM " + TABLE_DRUG;

        Cursor cursor = db.rawQuery(query, null);

        boolean result = false;

        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0) == 1;
            cursor.close();
        }

        db.close();

        return result;
    }

    public List<DrugTime> getTakingMedicineByTime() {
        List<DrugTime> drugTimes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DRUG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String drugName = cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME));
                String amountDrug = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT_DRUG));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME));
                String timeEat = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT));
                boolean eatActive = cursor.getInt(cursor.getColumnIndex(COLUMN_EAT_ACTIVE)) == 1;
                boolean beforeTime = cursor.getInt(cursor.getColumnIndex(COLUMN_BEFORE_TIME)) == 1;
                boolean afterTime = cursor.getInt(cursor.getColumnIndex(COLUMN_AFTER_TIME)) == 1;

                DrugTime drugTime = new DrugTime(id, drugName, amountDrug, startTime, endTime, timeEat, eatActive, beforeTime, afterTime);
                drugTimes.add(drugTime);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return drugTimes;
    }

    public List<DrugTime> getAllDrugtime() {
        List<DrugTime> drugTimes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DRUG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String drugName = cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME));
                String amountDrug = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT_DRUG));
                String timeEat = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT));

                DrugTime drugTime = new DrugTime(id, drugName, amountDrug, timeEat);
                drugTimes.add(drugTime);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return drugTimes;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void deleteExpiredDrugTimes() {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentTime = getCurrentTime();

        Log.d("DrugDatabaseHelper", "Current Time: " + currentTime);

        int deletedRows = db.delete(TABLE_DRUG, COLUMN_END_TIME + " <= ?", new String[]{currentTime});
        Log.d("DrugDatabaseHelper", "Deleted " + deletedRows + " expired drug times");

        db.close();
    }

    public List<DrugTime> getAllData() {
        List<DrugTime> drugTimes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DRUG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String drugName = cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME));
                String amountDrug = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT_DRUG));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME));
                String timeEat = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT));
                boolean eatActive = cursor.getInt(cursor.getColumnIndex(COLUMN_EAT_ACTIVE)) == 1;
                boolean beforeTime = cursor.getInt(cursor.getColumnIndex(COLUMN_BEFORE_TIME)) == 1;
                boolean afterTime = cursor.getInt(cursor.getColumnIndex(COLUMN_AFTER_TIME)) == 1;

                DrugTime drugTime = new DrugTime(id, drugName, amountDrug, startTime, endTime, timeEat, eatActive, beforeTime, afterTime);
                drugTimes.add(drugTime);

                Log.d("DrugDatabaseHelper", "ID: " + id +
                        ", Drug Name: " + drugName +
                        ", Amount Drug: " + amountDrug +
                        ", Start Time: " + startTime +
                        ", End Time: " + endTime +
                        ", Time Eat: " + timeEat +
                        ", Eat Active: " + eatActive +
                        ", Before Time: " + beforeTime +
                        ", After Time: " + afterTime);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return drugTimes;
    }

    public List<TakingMedicine> getAllTakingMedicineFromDrugData() {
        List<TakingMedicine> takingMedicineList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DRUG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String drugName = cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME));
                String timeEat = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT));
                boolean eatActive = cursor.getInt(cursor.getColumnIndex(COLUMN_EAT_ACTIVE)) == 1;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("th", "TH"));
                String createdAt = dateFormat.format(new Date());

                TakingMedicine takingMedicine = new TakingMedicine(drugName, timeEat, eatActive, createdAt);
                takingMedicineList.add(takingMedicine);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return takingMedicineList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DRUG, null, null);
        db.close();
    }
}