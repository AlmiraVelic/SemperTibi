package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDao
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalOverview : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var fabButton: ExtendedFloatingActionButton
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_overview)

        initViews()
        initUserDao()

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateString = dateFormat.format(currentDate)

        GlobalData.dateInCalendar = dateString

        updateFabBtn(dateString)

        observeDateChanges()

        fabButton.setOnClickListener {
            val intent = Intent(this, MoodJournalNew::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendarView)
        fabButton = findViewById(R.id.ext_fab)
    }

    private fun initUserDao() {
        userDao = UserDatabase.getInstance(this).userDao()
    }


    private fun observeDateChanges() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateInCalendar = formatDate(dayOfMonth, month, year)
            GlobalData.dateInCalendar = dateInCalendar

            lifecycleScope.launch {
                val existingEntry = withContext(Dispatchers.IO) {
                    userDao.readMoodOfUser(GlobalData.userID!!, dateInCalendar)
                }

                if (existingEntry == null) {
                    fabButton.setIconResource(R.drawable.ic_add)
                    fabButton.text = getString(R.string.newEntry)
                } else {
                    GlobalData.moodEntryID = existingEntry.entry_id
                    GlobalData.moodEntryID = existingEntry.entry_id
                    fabButton.setIconResource(R.drawable.ic_edit)
                    fabButton.text = getString(R.string.editEntry)
                }
            }
        }
    }

    private fun formatDate(dayOfMonth: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun updateFabBtn(date: String) {
        lifecycleScope.launch {
            val existingEntry = withContext(Dispatchers.IO) {
                userDao.readMoodOfUser(GlobalData.userID!!, date)
            }

            if (existingEntry == null) {
                fabButton.setIconResource(R.drawable.ic_add)
                fabButton.text = getString(R.string.newEntry)
            } else {
                GlobalData.moodEntryID = existingEntry.entry_id
                fabButton.setIconResource(R.drawable.ic_edit)
                fabButton.text = getString(R.string.editEntry)
            }
        }
    }
}
