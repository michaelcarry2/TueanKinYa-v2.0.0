package com.example.tueankinya;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.tueankinya.dao.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView addappointment;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public AppointmentFragment() {
        // Required empty public constructor
    }

    public static AppointmentFragment newInstance(String param1, String param2) {
        AppointmentFragment fragment = new AppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        Button cancelButton = view.findViewById(R.id.button_cancel_appoinement);
        Button saveButton = view.findViewById(R.id.button_save_appointment_profile);

        addappointment = view.findViewById(R.id.add_date_appointment_profile);

        addappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(addappointment, null, true);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileFragment();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointmentData(view);
            }
        });
        displayAppointmentData(view);
        return view;
    }

    private void navigateToProfileFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void showDatePicker(final TextView targetTextView, String startDateString, final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar startDateCalendar = null;
        if (startDateString != null && !startDateString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            try {
                Date startDate = sdf.parse(startDateString);
                if (startDate != null) {
                    startDateCalendar = Calendar.getInstance();
                    startDateCalendar.setTime(startDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String selectedDate = selectedDay + " " + getMonthName(selectedMonth) + " " + selectedYear;
                        targetTextView.setText(selectedDate);

                        if (isStartTime) {
                            targetTextView.setBackgroundResource(R.drawable.active_border);
                        } else {
                            targetTextView.setBackgroundResource(R.drawable.active_border);
                        }
                    }
                },
                year, month, day
        );

        if (startDateCalendar != null) {
            datePickerDialog.getDatePicker().setMaxDate(startDateCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void displayAppointmentData(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"appointment_date", "hospitalappointment"};
        Cursor cursor = db.query(
                "appointment_table",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String appointmentDate = cursor.getString(cursor.getColumnIndex("appointment_date"));
                @SuppressLint("Range") String hospitalappointment = cursor.getString(cursor.getColumnIndex("hospitalappointment"));
                TextView textView = new TextView(getActivity());
                textView.setText("Appointment Date: " + appointmentDate + ", Hospital: " + hospitalappointment);

            } while (cursor.moveToNext());
        } else {

            TextView noDataTextView = new TextView(getActivity());
            ((ViewGroup) view).addView(noDataTextView);
        }

        cursor.close();
        db.close();
    }


    private void saveAppointmentData(View view) {
        TextView dateTextView = view.findViewById(R.id.add_date_appointment_profile);
        String selectedDate = dateTextView.getText().toString().trim();

        EditText hospitalEditText = view.findViewById(R.id.button_add_hospital_apponitment_profile);
        String hospitalappointment = hospitalEditText.getText().toString().trim();

        if (!selectedDate.isEmpty() && !hospitalappointment.isEmpty()) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                Cursor cursor = db.query(
                        "appointment_table",
                        null,
                        "appointment_date = ?",
                        new String[]{selectedDate},
                        null,
                        null,
                        null
                );

                if (cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put("hospitalappointment", hospitalappointment);

                    int rowsAffected = db.update(
                            "appointment_table",
                            values,
                            "appointment_date = ?",
                            new String[]{selectedDate}
                    );

                    if (rowsAffected > 0) {
                        showSuccessDialog();
                    } else {
                        showErrorDialog("Failed to update appointment data in the database.");
                    }
                } else {
                    ContentValues values = new ContentValues();
                    values.put("appointment_date", selectedDate);
                    values.put("hospitalappointment", hospitalappointment);

                    long newRowId = db.insert("appointment_table", null, values);

                    if (newRowId != -1) {
                        showSuccessDialog();
                    } else {
                        showErrorDialog("Failed to insert data into the database.");
                    }
                }

                cursor.close();
                db.close();
            } catch (Exception e) {
                showErrorDialog("Exception: " + e.getMessage());
            }
        } else {
            showToast("กรุณากรอกข้อมูลให้ครบถ้วน");
        }
    }

    private void showSuccessDialog() {

        Toast.makeText(getActivity(), "เพิ่มการนัดหมายสำเร็จ", Toast.LENGTH_SHORT).show();

        navigateToProfileFragment();
    }


    private void showErrorDialog(String errorMessage) {
        Toast.makeText(getActivity(), "เพิ่มการนัดหมายไม่สำเร็จ", Toast.LENGTH_SHORT).show();

        navigateToProfileFragment();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private String getMonthName(int month) {
        String[] monthNames = {"ม.ค", "ก.พ", "มี.ค", "เม.ย", "พ.ค", "มิ.ย", "ก.ค", "ส.ค", "ก.ย", "ต.ค", "พ.ย", "ธ.ค"};
        return monthNames[month];
    }
}