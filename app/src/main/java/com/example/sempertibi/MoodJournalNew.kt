package com.example.sempertibi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDao
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.MoodJournal
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodJournalNew : AppCompatActivity() {

    private lateinit var moodSituationInput: TextInputEditText
    private lateinit var moodEmotionInput: TextInputEditText
    private lateinit var moodAchievementInput: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_new)

        initViews()
        initUserDao()

        updateTextViews(GlobalData.dateInCalendar!!)

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                //  If either of these values is null, then the dao.readMoodOfUser() method will not be called, and existingEntry will be null.
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

                    val moodJournal =
                        MoodJournal(
                            0,
                            GlobalData.userID!!,
                            GlobalData.dateInCalendar!!,
                            moodSituation,
                            moodEmotion,
                            moodAchievement,
                            1
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
                    Toast.makeText(applicationContext, "Data Saved", Toast.LENGTH_SHORT).show()

                    val moodJournal =
                        MoodJournal(
                            GlobalData.moodEntryID!!,
                            GlobalData.userID!!,
                            GlobalData.dateInCalendar!!,
                            moodSituation,
                            moodEmotion,
                            moodAchievement,
                            1
                        )

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            userDao.updateMood(moodJournal)
                        }
                    }
                }
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
    }

}