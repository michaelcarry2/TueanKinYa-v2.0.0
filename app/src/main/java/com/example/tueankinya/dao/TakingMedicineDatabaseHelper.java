package com.example.tueankinya.dao;

import static com.example.tueankinya.dao.DrugDatabaseHelper.TABLE_DRUG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;

import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.model.TakingMedicine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TakingMedicineDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taking.sqlite";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_TAKING_MEDICINE = "taking";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DRUG_NAME = "drug_name";
    public static final String COLUMN_TIME_EAT = "time_eat";
    public static final String COLUMN_EAT_ACTIVE = "eat_active";
    public static final String COLUMN_CREATE_AT = "create_at";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_TAKING_MEDICINE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DRUG_NAME + " TEXT,"
            + COLUMN_TIME_EAT + " TEXT,"
            + COLUMN_EAT_ACTIVE + " INTEGER,"
            + COLUMN_CREATE_AT + " TEXT"
            + ")";

    private Context context;

    public TakingMedicineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "onCreate called");
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Database", "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAKING_MEDICINE);
        onCreate(db);
    }

    public long insertTakingMedicine(TakingMedicine takingMedicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRUG_NAME, takingMedicine.getDrugName());
        values.put(COLUMN_TIME_EAT, takingMedicine.getTimeEat());
        values.put(COLUMN_EAT_ACTIVE, takingMedicine.getEatActive() != null && takingMedicine.getEatActive() ? 1 : 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("th", "TH"));
        String createdAt = dateFormat.format(new Date());
        values.put(COLUMN_CREATE_AT, createdAt);

        long rowId = db.insert(TABLE_TAKING_MEDICINE, null, values);
        db.close();
        return rowId;
    }

    public List<TakingMedicine> getTakingMedicineByTime(String currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TakingMedicine> takingMedicineList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TAKING_MEDICINE + " WHERE " + COLUMN_TIME_EAT + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{currentTime})) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    String drugName = cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME));
                    String timeEat = cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT));
                    boolean eatActive = "true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(COLUMN_EAT_ACTIVE)));
                    String createAt = cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_AT));

                    TakingMedicine takingMedicine = new TakingMedicine(drugName, timeEat, eatActive, createAt);
                    takingMedicineList.add(takingMedicine);
                } while (cursor.moveToNext());
            }
        }

        return takingMedicineList;
    }

    public List<TakingMedicine> getAllTakingMedicine() {
        List<TakingMedicine> takingMedicineList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TAKING_MEDICINE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TakingMedicine takingMedicine = new TakingMedicine();
                takingMedicine.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                takingMedicine.setDrugName(cursor.getString(cursor.getColumnIndex(COLUMN_DRUG_NAME)));
                takingMedicine.setTimeEat(cursor.getString(cursor.getColumnIndex(COLUMN_TIME_EAT)));
                takingMedicine.setEatActive(cursor.getInt(cursor.getColumnIndex(COLUMN_EAT_ACTIVE)) == 1);
                takingMedicine.setCreateAt(cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_AT)));
                takingMedicineList.add(takingMedicine);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return takingMedicineList;
    }

    public void insertAllTakingMedicineFromDrugData() {
        DrugDatabaseHelper drugDatabaseHelper = new DrugDatabaseHelper(context);
        List<TakingMedicine> takingMedicineList = drugDatabaseHelper.getAllTakingMedicineFromDrugData();

        TakingMedicineDatabaseHelper takingMedicineDatabaseHelper = new TakingMedicineDatabaseHelper(context);
        for (TakingMedicine takingMedicine : takingMedicineList) {
            takingMedicineDatabaseHelper.insertTakingMedicine(takingMedicine);
        }
    }
}
