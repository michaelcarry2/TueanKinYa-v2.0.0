package com.example.tueankinya;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.databinding.ActivityMainBinding;
import com.example.tueankinya.model.DrugTime;
import com.example.tueankinya.receiver.DailyReceiver;
import com.example.tueankinya.service.MyForegroundService;
import com.example.tueankinya.service.WorkerClass;
import com.example.tueankinya.utils.TimeCalculator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DrugDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DrugDatabaseHelper(this);
        replaceFragment(new HomeFragment());
        createNotificationChannel();
        deleteExpiredDrugTimes();
        getAllNotificationData();
        checkAndSetAlarmForAllDrugs();
        Intent foregroundServiceIntent = new Intent(this, MyForegroundService.class);
        startService(foregroundServiceIntent);

        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_MAIN.equals(intent.getAction())) {
            Log.d("MainActivity", "Application opened!");
        }

        setAlarm();

        dbHelper = new DrugDatabaseHelper(this);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.notification) {
                replaceFragment(new NotificationFragment());
            } else if (itemId == R.id.history) {
                replaceFragment(new HistoryFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commitNow();
    }

    private void deleteExpiredDrugTimes() {
        try (DrugDatabaseHelper dbHelper = new DrugDatabaseHelper(this)) {
            dbHelper.deleteExpiredDrugTimes();
            Log.d("MainActivity", "Expired drug times deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllNotificationData() {
        try (DrugDatabaseHelper dbHelper = new DrugDatabaseHelper(this)) {
            List<DrugTime> drugTimes = dbHelper.getAllDrugtime();

            if (drugTimes != null && !drugTimes.isEmpty()) {
                Log.e("MainActivity", "Notification times set:");
                for (DrugTime drugTime : drugTimes) {
                    String drugName = drugTime.getDrugName() != null ? drugTime.getDrugName() : "N/A";
                    String startTime = drugTime.getStartTime() != null ? drugTime.getStartTime() : "N/A";
                    String endTime = drugTime.getEndTime() != null ? drugTime.getEndTime() : "N/A";
                    String timeEat = drugTime.getTimeEat() != null ? drugTime.getTimeEat() : "N/A";

                    Log.e("MainActivity", "Drug: " + drugName +
                            ", Start Time: " + startTime +
                            ", End Time: " + endTime +
                            ", eat Time: " + timeEat);

                    setAlarmUsingWorkManager(drugTime);
                }
            } else {
                Log.e("MainActivity", "No notification times set.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkAndSetAlarmForAllDrugs() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        boolean isAlarmSet = preferences.getBoolean("isAlarmSet", false);

        if (!isAlarmSet) {
            List<DrugTime> drugTimes = dbHelper.getAllDrugtime();

            if (drugTimes != null && !drugTimes.isEmpty()) {
                for (DrugTime drugTime : drugTimes) {
                    setAlarmUsingWorkManager(drugTime);
                }

                saveAlarmSetting();
            }
        }
    }

    private void saveAlarmSetting() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAlarmSet", true);
        editor.apply();
    }

    private void setAlarmUsingWorkManager(DrugTime drugTime) {
        Calendar calendar = TimeCalculator.calculateAlarmTime(drugTime);

        if (calendar != null) {
            String drugName = drugTime.getDrugName() != null ? drugTime.getDrugName() : "N/A";

            long delayInMillis = Math.max(calendar.getTimeInMillis() - System.currentTimeMillis(), 0);

            Data inputData = new Data.Builder()
                    .putString("drugName", drugName)
                    .putBoolean("notificationClicked", false)
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkerClass.class)
                    .setConstraints(constraints)
                    .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

            SimpleDateFormat logDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());
            String logSetTimeMessage = "Setting alarm for drug: " + drugName +
                    ", Time to set: " + logDateFormat.format(calendar.getTime());
            Log.e("MainActivity", logSetTimeMessage);

            Toast.makeText(this, "Alarm set for drug: " + drugName +
                    " at " + logDateFormat.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

            Log.e("MainActivity", "Alarm set for drug: " + drugName +
                    " at " + logDateFormat.format(calendar.getTime()));

            Log.d("MainActivity", "WorkerClass enqueued for drug: " + drugName);
        } else {
            Log.e("MainActivity", "Alarm time is null");
            Toast.makeText(this, "Error setting alarm. Please check the date and time.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            String description = "Channel for my app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {
        Intent alarmIntent = new Intent(this, DailyReceiver.class);
        alarmIntent.setAction("com.example.tueankinya.ALARM_BROADCAST");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}