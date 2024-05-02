package com.example.tueankinya.utils;

import android.util.Log;

import com.example.tueankinya.model.DrugTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeCalculator {
    public static long calculateDays(DrugTime drugTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("th", "TH"));
        try {
            Date dateStart = sdf.parse(drugTime.getStartTime());
            Date dateEnd = sdf.parse(drugTime.getEndTime());

            long diffInMillies = Math.abs(dateEnd.getTime() - dateStart.getTime());
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("TimeCalculator", "Error parsing date: " + e.getMessage());
            return -1;
        }
    }

    public static Calendar calculateAlarmTime(DrugTime drugTime) {
        String selectedTime = drugTime.getTimeEat();

        if (!selectedTime.equals("none")) {
            SimpleDateFormat sdfInput = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            try {
                Date date = sdfInput.parse(selectedTime);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                calendar.set(Calendar.SECOND, 0);

                Log.d("TimeCalculator", "Parsed Calendar: " + calendar.getTime().toString());
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("TimeCalculator", "Error parsing date: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}