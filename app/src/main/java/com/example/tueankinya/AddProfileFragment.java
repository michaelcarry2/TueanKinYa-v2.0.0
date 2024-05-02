package com.example.tueankinya;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.tueankinya.dao.DatabaseHelper;

public class AddProfileFragment extends Fragment {

    private EditText nameEditText, sicknessEditText, hospitalEditText;
    private Button saveButton, cancelButton;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_profile, container, false);

        nameEditText = view.findViewById(R.id.button_add_name_profile);
        sicknessEditText = view.findViewById(R.id.button_add_sickness_profile);
        hospitalEditText = view.findViewById(R.id.button_add_hotpita_profile);
        saveButton = view.findViewById(R.id.button_save_profile);
        cancelButton = view.findViewById(R.id.button_cancel_profile);

        dbHelper = new DatabaseHelper(getActivity());


        displayProfileData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileFragment();
            }
        });

        return view;
    }

    private void displayProfileData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("profile_table", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String sickness = cursor.getString(cursor.getColumnIndex("sickness"));
            @SuppressLint("Range") String hospital = cursor.getString(cursor.getColumnIndex("hospital"));

            nameEditText.setText(name);
            sicknessEditText.setText(sickness);
            hospitalEditText.setText(hospital);
        }

        cursor.close();
        db.close();
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String sickness = sicknessEditText.getText().toString().trim();
        String hospital = hospitalEditText.getText().toString().trim();

        if (!name.isEmpty() && !sickness.isEmpty() && !hospital.isEmpty()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("sickness", sickness);
            values.put("hospital", hospital);


            Cursor cursor = db.query("profile_table", null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                // ถ้ามีข้อมูลโปรไฟล์ในฐานข้อมูลให้ทำการอัปเดต
                int rowsUpdated = db.update("profile_table", values, null, null);

                if (rowsUpdated > 0) {
                    showSuccessDialog();
                } else {

                    showErrorDialog("Failed to update profile.");
                }
            } else {

                long newRowId = db.insert("profile_table", null, values);

                if (newRowId != -1) {
                    showSuccessDialog();
                } else {

                    showErrorDialog("Failed to insert profile data.");
                }
            }

            cursor.close();
            db.close();
        } else {

            showErrorDialog("Please fill in all fields.");
        }
    }

    private void showSuccessDialog() {

        Toast.makeText(getActivity(), "เพิ่มข้อมูลส่วนตัวสำเร็จ", Toast.LENGTH_SHORT).show();

        navigateToProfileFragment();
    }


    private void showErrorDialog(String errorMessage) {
        Toast.makeText(getActivity(), "เพิ่มข้อมูลส่วนตัวไม่สำเร็จ", Toast.LENGTH_SHORT).show();

        navigateToProfileFragment();
    }

    private void navigateToProfileFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}