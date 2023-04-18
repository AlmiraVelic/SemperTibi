package com.example.sempertibi

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {

    // Notifications to the User as Reminder to use the App (As this app needs to be tested for the Master Thesis)
    // Called when the application is starting, before any other application objects have been created.
    override fun onCreate() {
        // Dark mode off
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate()
        // Create the NotificationChannel
        val channelID = "Reminder"
        val channelName = getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, channelName, importance)
        // Register the channel with the system
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    // Global Data should be cleared when User terminates the App
    fun clearGlobalData() {
        GlobalData.userID = null
        GlobalData.loggedInUser = null
        GlobalData.passwordUser = null
        GlobalData.emailUser = null
        GlobalData.notificationUser = null
        GlobalData.genderUser = null
        GlobalData.pssScore = 0
        GlobalData.hrvScore = 0
        GlobalData.dateToday = null
    }

    override fun onTerminate() {
        super.onTerminate()
        clearGlobalData()
    }

}