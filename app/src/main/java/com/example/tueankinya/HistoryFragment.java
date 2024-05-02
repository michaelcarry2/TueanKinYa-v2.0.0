package com.example.tueankinya;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.utils.TimeCalculator;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tueankinya.adapter.HistoryAdapter;
import com.example.tueankinya.model.DrugTime;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.widget.Button;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;

    private ImageView deleteDrug;
    public HistoryFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.history_item);
        historyAdapter = new HistoryAdapter(getDrugTimeListFromDatabase(), getContext());
        historyRecyclerView.setAdapter(historyAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    private List<DrugTime> getDrugTimeListFromDatabase() {
        DrugDatabaseHelper dbHelper = new DrugDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DrugDatabaseHelper.TABLE_DRUG;

        Cursor cursor = db.rawQuery(query, null);

        List<DrugTime> drugTimeList = new ArrayList<>();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_ID));
                    String drugName = cursor.getString(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_DRUG_NAME));
                    String amountDrug = cursor.getString(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_AMOUNT_DRUG));
                    String startTime = cursor.getString(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_START_TIME));
                    String endTime = cursor.getString(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_END_TIME));
                    String timeEat = cursor.getString(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_TIME_EAT));
                    boolean eatActive = cursor.getInt(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_EAT_ACTIVE)) == 1;
                    boolean beforeTime = cursor.getInt(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_BEFORE_TIME)) == 1;
                    boolean afterTime = cursor.getInt(cursor.getColumnIndex(DrugDatabaseHelper.COLUMN_AFTER_TIME)) == 1;

                    DrugTime drugTime = new DrugTime(id, drugName,amountDrug, startTime, endTime, timeEat, eatActive, beforeTime, afterTime);
                    drugTimeList.add(drugTime);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return drugTimeList;
    }
}