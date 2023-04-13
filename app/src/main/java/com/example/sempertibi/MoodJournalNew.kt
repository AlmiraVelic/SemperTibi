package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDao
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.MoodJournal
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodJournalNew : AppCompatActivity() {

    private lateinit var moodSituationInput: TextInputEditText
    private lateinit var moodEmotionInput: TextInputEditText
    private lateinit var moodAchievementInput: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var userDao: UserDao
    private lateinit var tvNote: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_new)
        StrictMode.enableDefaults()

        initViews()
        initUserDao()

        updateTextViews(GlobalData.dateInCalendar!!)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuDashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuMoodJournal -> {
                    val intent = Intent(this, MoodJournalOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuStressTracker -> {
                    val intent = Intent(this, StressTrackerOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuToDoList -> {
                    val intent = Intent(this, ToDoList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuSignOut -> {
                    AlertDialog.Builder(this).setTitle("Sign Out")
                        .setMessage("Do you really want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Handle sign out here
                            val myApplication = applicationContext as MyApplication
                            myApplication.clearGlobalData()
                            val packageManager = applicationContext.packageManager
                            val intent =
                                packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                            val componentName = intent!!.component
                            val mainIntent = Intent.makeRestartActivityTask(componentName)
                            applicationContext.startActivity(mainIntent)
                        }
                        .setNegativeButton("No"){_,_->
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }


        btnSave.setOnClickListener {
            lifecycleScope.launch {

                val existingEntry = withContext(Dispatchers.IO) {
                    GlobalData.userID?.let { userID ->
                        GlobalData.dateInCalendar?.let { dateInCalendar ->
                            userDao.readMoodOfUser(userID, dateInCalendar)
                        }
                    }
                }

                if (existingEntry == null) {
                    val moodSituation = moodSituationInput.text.toString()
                    val moodEmotion = moodEmotionInput.text.toString()
                    val moodAchievement = moodAchievementInput.text.toString()
                    var moodValue = 0

                    if (GlobalData.dateInCalendar.equals(GlobalData.dateToday)){
                        moodValue = GlobalData.moodValue
                    }

                    val moodJournal =
                        MoodJournal(
                            0,
                            GlobalData.userID!!,
                            GlobalData.dateInCalendar!!,
                            moodSituation,
                            moodEmotion,
                            moodAchievement,
                            moodValue
                        )

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            userDao.addMood(moodJournal)
                        }
                    }
                } else {
                    val moodSituation = moodSituationInput.text.toString()
                    val moodEmotion = moodEmotionInput.text.toString()
                    val moodAchievement = moodAchievementInput.text.toString()

                    val moodJournal =
                        MoodJournal(
                            GlobalData.moodEntryID!!,
                            GlobalData.userID!!,
                            GlobalData.dateInCalendar!!,
                            moodSituation,
                            moodEmotion,
                            moodAchievement,
                            existingEntry.mood_score
                        )

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            userDao.updateMood(moodJournal)
                        }
                    }
                }
                Toast.makeText(applicationContext, "Data Saved", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@MoodJournalNew, MoodJournalOverview::class.java)
                startActivity(intent)
            }
        }

        val icon = findViewById<ImageView>(R.id.logo)
        icon.bringToFront()

        val scrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.GONE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }

    private fun initViews() {
        // Entry fields
        moodSituationInput = findViewById(R.id.tvSituationInput)
        moodEmotionInput = findViewById(R.id.tvEmotionInput)
        moodAchievementInput = findViewById(R.id.tvAchievementInput)
        // Button
        btnSave = findViewById(R.id.btnSaveEntry)
        tvNote = findViewById(R.id.tvHintMJ)
    }

    private fun initUserDao() {
        userDao = UserDatabase.getInstance(this).userDao()
    }

    private fun updateTextViews(date: String) {
        lifecycleScope.launch {
            val existingEntry = withContext(Dispatchers.IO) {
                userDao.readMoodOfUser(GlobalData.userID!!, date)
            }

            if (existingEntry != null) {
                moodSituationInput.setText(existingEntry.situation)
                moodEmotionInput.setText(existingEntry.emotion)
                moodAchievementInput.setText(existingEntry.achievement)
                btnSave.text = getString(R.string.editEntry)
            }
        }

        if (date != getTodayDate()){
            tvNote.visibility = View.VISIBLE
        }
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        calendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}