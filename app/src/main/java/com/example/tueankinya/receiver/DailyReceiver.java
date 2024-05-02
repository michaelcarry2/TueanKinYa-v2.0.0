package com.example.tueankinya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.dao.TakingMedicineDatabaseHelper;
import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.model.TakingMedicine;

import java.util.List;

public class DailyReceiver extends BroadcastReceiver {
    private DrugDatabaseHelper dbHelper;
    private TakingMedicineDatabaseHelper dbHelperTakingMedicine;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            dbHelper = new DrugDatabaseHelper(context);
            dbHelperTakingMedicine = new TakingMedicineDatabaseHelper(context);

            dbHelperTakingMedicine.insertAllTakingMedicineFromDrugData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dbHelper.setAllToDefault();
                }
            }, 2000);
        }
    }
}