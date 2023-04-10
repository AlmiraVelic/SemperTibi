package com.example.sempertibi

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * use a broadcast receiver for handling the pending intent.
 * Let’s create the AlarmReceiver class at the root of the project.
 * This receiver then contains the logic for showing the notification.
 */

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: return
        showNotification(context, message)
    }

    private fun showNotification(context: Context?, message: String) {

        // Create the notification
        val channelId = "Reminder" // Create a unique channel ID for your app
        // Intent to open the Main activity when clicking on the notification
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context!!, channelId)
            .setContentTitle("Reminder")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 1000))
            .setContentIntent(pendingIntent) // Set the intent to open the app
            .setAutoCancel(true) // Dismiss the notification when the user clicks on it
            .build()

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)

        // Check if the app has the necessary permissions to show the notification
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(0, notification)
        }
    }
}
