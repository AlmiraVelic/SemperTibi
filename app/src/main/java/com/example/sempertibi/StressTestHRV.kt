package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class StressTestHRV : AppCompatActivity() {
    private lateinit var linkTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_test_hrv)

        // link in Text should be clickable
        linkTextView = findViewById(R.id.hrvTextView)
        linkTextView.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Notification")
                .setMessage("You are leaving the app now to a 3rd party website")
                .setPositiveButton("Ok") { _, _ ->
                    linkTextView.movementMethod = LinkMovementMethod.getInstance()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}