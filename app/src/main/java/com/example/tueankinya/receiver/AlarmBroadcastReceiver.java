package com.example.tueankinya.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.tueankinya.MainActivity;
import com.example.tueankinya.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String drugName = intent.getStringExtra("drugName");
            if (drugName != null) {
                Log.d("AlarmBroadcastReceiver", "Drug: " + drugName + ", Notification clicked!");
                showNotification(context, drugName);
            }
        }
    }

    private void showNotification(Context context, String drugName) {
        int notificationId = 1;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.mipmap.tuean_kin_ya_launcher)
                .setContentTitle("เตือนกินยา")
                .setContentText("กรุณากินยา: " + drugName)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}