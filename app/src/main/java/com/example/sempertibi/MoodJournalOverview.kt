package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView

class MoodJournalOverview : AppCompatActivity() {
    lateinit var calendarView: CalendarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_overview)
        val newEntry = findViewById<Button>(R.id.btnNewEntry)

        calendarView = findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var date: String = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)

            newEntry.setOnClickListener {
                val intent = Intent(this, MoodJournalNew::class.java)
                intent.putExtra("DATE", date)
                startActivity(intent)
            }

        }
    }
}