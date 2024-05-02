package com.example.tueankinya;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.tueankinya.adapter.HomeAdapter;
import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.dao.TakingMedicineDatabaseHelper;
import com.example.tueankinya.model.TakingMedicine;
import com.example.tueankinya.utils.TimeCalculator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tueankinya.model.DrugTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private Handler handler;
    private Runnable runnable;
    private CountDownTimer countDownTimer;
    private List<DrugTime> drugTimeList;
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView homeRecyclerView;
    private HomeAdapter homeAdapter;
    private TextView currentDateTextView, currentTimeTextView;
    private Button setToDefault;
    private DrugDatabaseHelper dbHelper;
    private TakingMedicineDatabaseHelper dbHelperTakingMedicine;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        currentDateTextView = view.findViewById(R.id.current_date);
        setCurrentDate();

        currentTimeTextView = view.findViewById(R.id.current_time);
        startCountdownTimer();

        dbHelperTakingMedicine = new TakingMedicineDatabaseHelper(getContext());

        homeRecyclerView = view.findViewById(R.id.home_item);
        dbHelper = new DrugDatabaseHelper(getContext());

        homeAdapter = new HomeAdapter(getDrugTimeListFromDatabase(), dbHelper);
        homeRecyclerView.setAdapter(homeAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        homeRecyclerView.setLayoutManager(layoutManager);

        drugTimeList = getDrugTimeListFromDatabase();

        return view;
    }

    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("th", "TH"));
        String currentDate = dateFormat.format(new Date());
        currentDateTextView.setText(currentDate);
    }

    private List<DrugTime> getDrugTimeListFromDatabase() {
        DrugDatabaseHelper dbHelper = new DrugDatabaseHelper(getContext());
        List<DrugTime> drugTimeList = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String query = "SELECT * FROM " + DrugDatabaseHelper.TABLE_DRUG;
            try (Cursor cursor = db.rawQuery(query, null)) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_ID);
                    int drugNameIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_DRUG_NAME);
                    int amountDrugIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_AMOUNT_DRUG);
                    int startTimeIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_START_TIME);
                    int endTimeIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_END_TIME);
                    int timeEatIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_TIME_EAT);
                    int eatActiveIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_EAT_ACTIVE);
                    int beforeTimeIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_BEFORE_TIME);
                    int afterTimeIndex = cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_AFTER_TIME);

                    if (idIndex != -1 && drugNameIndex != -1 && amountDrugIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1
                            && timeEatIndex != -1 && eatActiveIndex != -1 && beforeTimeIndex != -1 && afterTimeIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String drugName = cursor.getString(drugNameIndex);
                        String amountDrug = cursor.getString(amountDrugIndex);
                        String startTime = cursor.getString(startTimeIndex);
                        String endTime = cursor.getString(endTimeIndex);
                        String timeEat = cursor.getString(timeEatIndex);
                        boolean eatActive = cursor.getInt(eatActiveIndex) == 1;
                        boolean beforeTime = cursor.getInt(beforeTimeIndex) == 1;
                        boolean afterTime = cursor.getInt(afterTimeIndex) == 1;

                        DrugTime drugTime = new DrugTime(id, drugName,amountDrug, startTime, endTime, timeEat, eatActive, beforeTime, afterTime);
                        drugTimeList.add(drugTime);
                    } else {
                        Log.e("HomeFragment", "Column not found in the cursor");
                    }
                }
            }
        }

        return drugTimeList;
    }


    public void startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                String formattedTime = getCurrentTime();
                currentTimeTextView.setText(formattedTime);
            }

            @Override
            public void onFinish() {
                currentTimeTextView.setText("");
            }
        }.start();
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(System.currentTimeMillis());
    }
}