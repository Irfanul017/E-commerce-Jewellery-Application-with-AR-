package com.example.majorproject.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the intent action is to cancel the notification
        if ("CANCEL_NOTIFICATION".equals(intent.getAction())) {
            // Get the unique notification ID passed through the intent
            int notificationId = intent.getIntExtra("notificationId", 0);

            // Log the received notification ID
            Log.d(TAG, "Canceling notification with ID: " + notificationId);

            // Use the NotificationManagerCompat to cancel the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);

            // Optionally, you can add logic to reset SharedPreferences or perform other tasks
            // For example, resetting the flag that tracks whether the notification has been sent
            // SharedPreferences prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE);
            // SharedPreferences.Editor editor = prefs.edit();
            // editor.putBoolean("notification_sent", false);
            // editor.apply();
        }
    }
}
