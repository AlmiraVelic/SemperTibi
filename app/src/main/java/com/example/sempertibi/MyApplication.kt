package com.example.sempertibi

import android.app.Application

/*
TODO check the Manifest implementation for this class, so app does not crash
 */
class MyApplication : Application() {

    private fun clearGlobalData() {
        GlobalData.userID = null
        GlobalData.loggedInUser = null
        GlobalData.passwordUser = null
        GlobalData.emailUser = null
        GlobalData.notificationUser = null
        GlobalData.genderUser = null

        /* TODO insert attributes from GlobalData.kt */
    }

    override fun onTerminate() {
        super.onTerminate()
        clearGlobalData()
    }

}