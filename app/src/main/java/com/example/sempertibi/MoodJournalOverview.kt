package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MoodJournalOverview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_overview)
        val newEntry = findViewById<Button>(R.id.btnNewEntry)
        newEntry.setOnClickListener{
            val intent = Intent(this, MoodJournalNew::class.java)
            startActivity(intent)
        }
    }
}