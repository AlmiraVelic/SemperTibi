package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.MoodJournal
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MoodJournalNew : AppCompatActivity() {

    lateinit var moodSituationInput: TextInputEditText
    lateinit var moodEmotionInput: TextInputEditText
    lateinit var moodAchievementInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_new)

        val dao = UserDatabase.getInstance(this).userDao()

        // Date of the entry
        val currentDate = SimpleDateFormat("dd-MM-yyy").format(Date())
        // Entry fields

        moodSituationInput = findViewById(R.id.tvSituationInput)
        moodEmotionInput = findViewById(R.id.tvEmotionInput)
        moodAchievementInput = findViewById(R.id.tvAchievementInput)


        val newEntry = findViewById<Button>(R.id.btnSaveEntry)
        newEntry.setOnClickListener {
            val moodSituation = moodSituationInput.text.toString()
            val moodEmotion = moodEmotionInput.text.toString()
            val moodAchievement = moodAchievementInput.text.toString()

            val insertMood = listOf(
                MoodJournal(0, 1, currentDate, moodSituation, moodEmotion, moodAchievement, 1)
            )

            lifecycleScope.launch {
                insertMood.forEach { dao.addMood(it) }
            }
            Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()

        }

        val icon = findViewById<ImageView>(R.id.logo)
        icon.bringToFront()

        val scrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }
}