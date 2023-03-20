package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

class MoodTracker : AppCompatActivity() {

    private lateinit var submitButton: Button
    private lateinit var moodRadioGroup: RadioGroup
    private lateinit var selectedRadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_tracker)

        submitButton = findViewById(R.id.submit_button)
        moodRadioGroup = findViewById(R.id.mood_radiogroup)

        submitButton.setOnClickListener {
            val selectedId = moodRadioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show()
            } else {
                selectedRadioButton = findViewById(selectedId)
                val selectedMood = selectedRadioButton.tag.toString()

                val moodValues = mapOf(
                    "happy" to 5,
                    "sad" to 1,
                    "angry" to 2,
                    "surprised" to 4,
                    "scared" to 3,
                    "disgusted" to 1
                )

                val moodValue = moodValues[selectedMood]
                GlobalData.moodValue = moodValue!!
                Toast.makeText(this, "You are feeling $selectedMood today", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
            }
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