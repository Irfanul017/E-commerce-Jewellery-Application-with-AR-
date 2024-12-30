package com.example.majorproject.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.majorproject.R
import com.example.majorproject.MainActivity // Example activity to be opened on notification tap

class NotificationHelper {

    private val notificationId = "mahavir_channel"
    private val TAG = "NotificationHelper"
    private val PREFS_NAME = "notification_prefs"
    private val NOTIFICATION_SENT_KEY = "notification_sent"

    // Create the notification channel (if needed)
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Mahavir Channel"
            val channelDescription = "Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(notificationId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    // Send the notification only once when the app is opened
    fun sendNotification(context: Context, notificationType: String, notificationContent: String, imageResId: Int) {
        // Check if the notification has been sent already
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notificationSent = sharedPreferences.getBoolean(NOTIFICATION_SENT_KEY, false)

        if (notificationSent) {
            Log.d(TAG, "Notification already sent. Skipping.")
            return
        }

        val uniqueNotificationId = notificationType.hashCode()

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_img_layout)
        remoteViews.setImageViewResource(R.id.notification_image, imageResId)

        val builder = NotificationCompat.Builder(context, notificationId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Notification will be dismissed when tapped
            .setCustomContentView(remoteViews)
            .setContentText(notificationContent)
            .setContentTitle(notificationType)

        // Add PendingIntent to cancel the notification when tapped
        val cancelIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "CANCEL_NOTIFICATION"
            putExtra("notificationId", uniqueNotificationId)
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder.setContentIntent(cancelPendingIntent)

        // Ensure POST_NOTIFICATIONS permission is granted for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(uniqueNotificationId, builder.build())
        Log.d(TAG, "Notification sent: $notificationType")

        // Mark the notification as sent by saving in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean(NOTIFICATION_SENT_KEY, true)
        editor.apply()
    }

    // Cancel the notification via BroadcastReceiver
    fun cancelNotification(context: Context, notificationType: String) {
        val uniqueNotificationId = notificationType.hashCode()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(uniqueNotificationId)
        Log.d(TAG, "Notification canceled: $notificationType")

        // Reset the flag so that the notification can be sent again
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(NOTIFICATION_SENT_KEY, false)
        editor.apply()
    }
}
