package com.example.tueankinya.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.tueankinya.MainActivity;
import com.example.tueankinya.R;
import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.model.DrugTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import com.example.tueankinya.R;
import com.example.tueankinya.receiver.AlarmBroadcastReceiver;
import com.example.tueankinya.utils.TimeCalculator;


public class WorkerClass extends Worker {

    public WorkerClass(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    private String getDrugName(int drugId) {
        return "ยาที่ " + drugId;
    }

    private static final String TAG = "WorkerClass";

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String drugNameFromInput = getInputData().getString("drugName");
        int drugId = getInputData().getInt("drugId", -1);

        Calendar now = Calendar.getInstance();

        DrugTime drugTime = getDrugTimeFromDatabase(drugNameFromInput);

        if (drugTime != null) {
            Calendar alarmTime = TimeCalculator.calculateAlarmTime(drugTime);

            if (now.get(Calendar.HOUR_OF_DAY) == alarmTime.get(Calendar.HOUR_OF_DAY)
                    && now.get(Calendar.MINUTE) == alarmTime.get(Calendar.MINUTE)) {

                Intent notificationIntent = new Intent(context, AlarmBroadcastReceiver.class);
                notificationIntent.setAction("NOTIFICATION_CLICKED");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

                showNotification(context, drugNameFromInput, drugId, pendingIntent);

                Intent broadcastIntent = new Intent(context, AlarmBroadcastReceiver.class);
                broadcastIntent.setAction("NOTIFICATION_CLICKED");
                broadcastIntent.putExtra("drugName", drugNameFromInput);
                context.sendBroadcast(broadcastIntent);

                String drugName = getDrugName(drugId);
                Log.d(TAG, "WorkerClass executed for drug: " + drugId + " (" + drugName + ")");

                return Result.success();
            } else {
                Log.d(TAG, "Not the right time for drug: " + drugNameFromInput);
                return Result.retry();
            }
        } else {
            Log.e(TAG, "Failed to retrieve DrugTime for drug: " + drugNameFromInput);
            return Result.failure();
        }
    }

    private DrugTime getDrugTimeFromDatabase(String drugName) {
        DrugDatabaseHelper dbHelper = new DrugDatabaseHelper(getApplicationContext());
        List<DrugTime> drugTimes = dbHelper.getAllDrugtime();
        for (DrugTime drugTime : drugTimes) {
            if (drugTime.getDrugName().equals(drugName)) {
                return drugTime;
            }
        }
        return null;
    }

    private void showNotification(Context context, String drugName, int drugId, PendingIntent pendingIntent) {
        int notificationId = 1;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                playDefaultNotificationSound(context);

                Intent openAppIntent = new Intent(context, MainActivity.class);
                openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                        .setSmallIcon(R.mipmap.tuean_kin_ya_launcher)
                        .setContentTitle("เตือนกินยา")
                        .setContentText("กรุณากินยา: " + drugName)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(openAppPendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationId, builder.build());

            }
        }, 4000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                playDefaultNotificationSound(context);
            }
        }, 9000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                playDefaultNotificationSound(context);
            }
        }, 12000);
    }

    private void playDefaultNotificationSound(Context context) {
        try {
            Uri notificationUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            Ringtone defaultRingtone = RingtoneManager.getRingtone(context, notificationUri);
            defaultRingtone.play();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error playing default notification sound: " + e.getMessage());
        }
    }

}