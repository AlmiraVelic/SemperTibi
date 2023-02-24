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
import kotlinx.coroutines.launch

class MoodJournalNew : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_new)

        val dao = UserDatabase.getInstance(this).userDao()

        val moodSituation = findViewById<TextInputEditText>(R.id.tvSituationInput).text.toString()
        val moodEmotion = findViewById<TextInputEditText>(R.id.tvEmotionInput).text.toString()
        val moodAchievement = findViewById<TextInputEditText>(R.id.tvAchievementInput).text.toString()

        val insertMood = listOf(
            MoodJournal(0,1, "01.01.2023", moodSituation, moodEmotion, moodAchievement, 1 )
        )

        val newEntry = findViewById<Button>(R.id.btnSaveEntry)
        newEntry.setOnClickListener{
            lifecycleScope.launch{
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