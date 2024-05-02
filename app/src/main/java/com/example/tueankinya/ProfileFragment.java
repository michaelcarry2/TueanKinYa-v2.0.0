package com.example.tueankinya;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tueankinya.dao.DatabaseHelper;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        displayProfileData(view);

        TextView addButton = view.findViewById(R.id.button_add_data_profile);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddProfileFragment();
            }
        });

        TextView addAppointmentButton = view.findViewById(R.id.button_add_appointment_data_profile);
        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddAppointmentFragment();
            }
        });
        TextView boxhistorydurg = view.findViewById(R.id.text_hitory_drug_profile);
        boxhistorydurg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHistorydrugFragment();
            }
        });

        return view;
    }

    private void displayProfileData(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursorProfile = db.query("profile_table", null, null, null, null, null, null);
        Cursor appointmentCursor = db.query("appointment_table", null, null, null, null, null, "id DESC", "1");

        if (cursorProfile != null && cursorProfile.moveToFirst()) {
            @SuppressLint("Range") String name = cursorProfile.getString(cursorProfile.getColumnIndex("name"));
            @SuppressLint("Range") String sickness = cursorProfile.getString(cursorProfile.getColumnIndex("sickness"));
            @SuppressLint("Range") String hospital = cursorProfile.getString(cursorProfile.getColumnIndex("hospital"));

            TextView nameTextView = view.findViewById(R.id.name_data_detail_profile);
            nameTextView.setText("ชื่อ - นามสกุล : " + name);

            TextView sicknessTextView = view.findViewById(R.id.name_sickness_data_detail_profile);
            sicknessTextView.setText("โรคประจำตัว : " + sickness);

            TextView hospitalTextView = view.findViewById(R.id.name_hospital_data_detail_profile);
            hospitalTextView.setText("โรงพยาบาล : " + hospital);

            cursorProfile.close();
        } else {
        }

        if (appointmentCursor != null && appointmentCursor.moveToFirst()){
            @SuppressLint("Range") String dateappointment = appointmentCursor.getString(appointmentCursor.getColumnIndex("appointment_date"));
            @SuppressLint("Range") String hospital = appointmentCursor.getString(appointmentCursor.getColumnIndex("hospitalappointment"));

            TextView dateappointmentTextView = view.findViewById(R.id.date_data_appointment_profile);
            dateappointmentTextView.setText("วันที่นัดครั้งต่อไป : " + dateappointment);

            TextView hospitalTextView = view.findViewById(R.id.name_hospital_appoinrment_data_detail_profile);
            hospitalTextView.setText("โรงพยาบาลที่นัด : " + hospital);

            appointmentCursor.close();
        } else {
        }

        db.close();
    }


    private void navigateToAddProfileFragment() {
        AddProfileFragment addProfileFragment = new AddProfileFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToAddAppointmentFragment() {
        AppointmentFragment appointmentFragment = new AppointmentFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, appointmentFragment)
                .addToBackStack(null)
                .commit();
    }
    private void navigateToHistorydrugFragment() {
        HistorydrugFragment historydrugFragment = new HistorydrugFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, historydrugFragment)
                .addToBackStack(null)
                .commit();
    }
}