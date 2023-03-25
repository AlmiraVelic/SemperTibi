package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.StressPSS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StressTestPSS : AppCompatActivity(), View.OnClickListener {

    private lateinit var informationText: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var tvQuestionCount: TextView
    private lateinit var rbGroup: RadioGroup
    private lateinit var rb1: RadioButton
    private lateinit var rb2: RadioButton
    private lateinit var rb3: RadioButton
    private lateinit var rb4: RadioButton
    private lateinit var rb5: RadioButton
    private lateinit var btnNext: Button
    private lateinit var btnRestart: Button
    private lateinit var btnSave: Button
    private lateinit var stressIndicatorTitle: TextView
    private lateinit var stressIndicatorArrow: ImageView
    private lateinit var stressIndicatorBar: View
    private lateinit var stressIndicatorScale: LinearLayout
    private lateinit var stressIndicatorLayout: RelativeLayout
    private lateinit var stressQuestionsLayout: LinearLayout
    private lateinit var tvAnswerPSS: TextView
    private lateinit var tvHintPSS: TextView


    private val questions = listOf(
        "In the last month, how often have you been upset because of something that happened unexpectedly?",
        "In the last month, how often have you felt that you were unable to control the important things in your life?",
        "In the last month, how often have you felt nervous and stressed?",
        "In the last month, how often have you felt confident about your ability to handle your personal problems?",
        "In the last month, how often have you felt that things were going your way?",
        "In the last month, how often have you found that you could not cope with all the things that you had to do?",
        "In the last month, how often have you been able to control irritations in your life?",
        "In the last month, how often have you felt that you were on top of things?",
        "In the last month, how often have you been angered because of things that happened that were outside of your control?",
        "In the last month, how often have you felt difficulties were piling up so high that you could not overcome them?"
    )

    private val answers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_test_pss)

        GlobalData.pssScore = 0

        initViews()

        btnNext.setOnClickListener(this)
        setQuestion(0)
        btnRestart.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun initViews() {
        stressQuestionsLayout = findViewById(R.id.stress_questions_layout)
        rbGroup = findViewById(R.id.radioGroup)
        rb1 = findViewById(R.id.radio_button_1)
        rb2 = findViewById(R.id.radio_button_2)
        rb3 = findViewById(R.id.radio_button_3)
        rb4 = findViewById(R.id.radio_button_4)
        rb5 = findViewById(R.id.radio_button_5)
        informationText = findViewById(R.id.informationText)
        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnNext = findViewById(R.id.btNext)

        stressIndicatorLayout = findViewById(R.id.stress_indicator_layout)
        stressIndicatorTitle = findViewById(R.id.stress_indicator_title)
        stressIndicatorArrow = findViewById(R.id.stress_indicator_arrow)
        stressIndicatorBar = findViewById(R.id.stress_indicator_bar)
        stressIndicatorScale = findViewById(R.id.stress_indicator_scale)
        tvAnswerPSS = findViewById(R.id.tvAnswerPSS)
        tvHintPSS = findViewById(R.id.tvHintPSS)
        btnRestart = findViewById(R.id.btRestart)
        btnSave = findViewById(R.id.btSave)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btNext -> {
                val checkedButton = rbGroup.findViewById<RadioButton>(rbGroup.checkedRadioButtonId)
                if (checkedButton != null) {
                    answers.add(rbGroup.indexOfChild(checkedButton))
                } else {
                    answers.add(-1)
                }
                rbGroup.clearCheck()

                val currentQuestionIndex = answers.size
                if (currentQuestionIndex < questions.size) {
                    setQuestion(currentQuestionIndex)
                } else {
                    finishTest()
                }
            }

            R.id.btRestart -> {
                // If user wants to restart the test for today
                AlertDialog.Builder(this).setTitle("Confirm")
                    .setMessage("Are you sure you want to restart the test?")
                    .setPositiveButton("Yes") { _, _ ->
                        // User confirmed
                        // Reset the activity if the button text is "Restart Test"
                        startActivity(Intent(this, StressTestPSS::class.java))
                        finish()
                    }.setNegativeButton("No", null).show()

            }
            R.id.btSave -> {
                Log.d("PSS", "Button clicked")
                // User confirmed
                val dao = UserDatabase.getInstance(this).userDao()
                Log.d("PSS", "DB accessed")
                // First, get the current date and time
                val currentDate = Date()
                Log.d("PSS", "Current Date set")
                // format the date to just the day, month, and year
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                Log.d("PSS", "Date format set")
                val dateString = dateFormat.format(currentDate)
                Log.d("PSS", "Date formated and set to dateString")

                val stressPSS = StressPSS(
                    testPSS_id = 0,
                    user_id = GlobalData.userID!!,
                    testPSS_date = dateString,
                    PSS_score = GlobalData.pssScore
                )
                Log.d("PSS", "New stressPSS object created")

                lifecycleScope.launch {
                    Log.d("PSS", "Coroutines lifecycleScope started")
                    // Check the existing entry in Database
                    val existingEntry = withContext(Dispatchers.IO) {
                        dao.getStressPSSByUserIdAndDate(
                            GlobalData.userID!!, dateString
                        )
                    }
                    Log.d("PSS", "get existing Entry")
                    // If there is no entry, pass the entry to the database
                    if (existingEntry == null) {
                        dao.addStressPSS(stressPSS)
                        Toast.makeText(
                            applicationContext, "Results saved", Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@StressTestPSS, StressTrackerOverview::class.java
                            )
                        )
                        Log.d("PSS", "New entry done")
                    } else {
                        // If there is an entry for this day, then user is notified
                        AlertDialog.Builder(this@StressTestPSS).setTitle("Duplicate entry")
                            .setMessage("There is already an entry for today. Do you want to update it?")
                            .setPositiveButton("Update") { _, _ ->
                                // update the existing entry
                                lifecycleScope.launch {
                                    dao.updateStressTestPSS(stressPSS)
                                }
                                Toast.makeText(
                                    applicationContext, "Results updated", Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@StressTestPSS, StressTrackerOverview::class.java
                                    )
                                )
                            }.setNegativeButton("Cancel") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@StressTestPSS, StressTrackerOverview::class.java
                                    )
                                )
                                Toast.makeText(
                                    applicationContext,
                                    "Results were not updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.show()
                    }
                }
            }

        }
    }

    private fun setQuestion(index: Int) {
        tvQuestion.text = questions[index]
        val myindex = index + 1
        tvQuestionCount.text = "Question $myindex/10"
        btnNext.text = if (index == questions.size - 1) "Finish" else "Next"
    }

    private fun finishTest() {
        // Reverse score the positively stated items 4,5,7,8
        answers[3] = 4 - answers[3]
        answers[4] = 4 - answers[4]
        answers[6] = 4 - answers[6]
        answers[7] = 4 - answers[7]

        // Calculate the PSS score using the answers
        var pssScore = 0

        for (answer in answers) when (answer) {
            0 -> {
                pssScore += 0
            }
            1 -> {
                pssScore += 1
            }
            2 -> {
                pssScore += 2
            }
            3 -> {
                pssScore += 3
            }
            4 -> {
                pssScore += 4
            }
        }

        // Return a text response based on the score
        val response = when (pssScore) {
            in 0..13 -> "You're doing well! Keep up the good work!"
            in 14..26 -> "You're experiencing moderate stress. Try to manage your stress levels."
            in 27..40 -> "You're experiencing high stress. Take some time to relax and seek support if needed."
            else -> "Invalid score"
        }

        // Add score to GlobalData for reuse
        GlobalData.pssScore = pssScore

        // Set Score and bar visible
        updateLayout(pssScore)

        // Display the PSS score
        tvAnswerPSS.text = "Your PSS score is $pssScore \n \n$response"


    }

    private fun updateLayout(pssScore: Int) {
        // Not needed Views and rbGroup set invisible
        stressQuestionsLayout.visibility = View.GONE
        btnNext.visibility = View.GONE

        // set Layout for stresslevel visible
        stressIndicatorLayout.visibility = View.VISIBLE
        stressIndicatorTitle.visibility = View.VISIBLE
        stressIndicatorArrow.visibility = View.VISIBLE
        stressIndicatorBar.visibility = View.VISIBLE
        stressIndicatorScale.visibility = View.VISIBLE
        tvAnswerPSS.visibility = View.VISIBLE

        // For saving the results a button is set visible
        btnRestart.visibility = View.VISIBLE
        btnSave.visibility = View.VISIBLE

        // set indicator on position of score
        val barWidth = stressIndicatorBar.width.toFloat()

        // Calculate the position of the arrow based on progress
        var arrowPosition = ((barWidth / 40) * pssScore)

        arrowPosition = when (pssScore) {
            in 0..13 -> arrowPosition
            in 14..19 -> arrowPosition - 15
            in 20..25 -> arrowPosition - 30
            in 25..30 -> arrowPosition - 40
            in 31..35 -> arrowPosition - 45
            else -> arrowPosition - 55
        }

        // Set the left margin of the arrow to the arrow position
        val layoutParams = stressIndicatorArrow.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart += arrowPosition.toInt()

        stressIndicatorArrow.layoutParams = layoutParams

        // Set Hint text visible
        tvHintPSS.visibility = View.VISIBLE
        tvHintPSS.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Notification")
                .setMessage("You are leaving the app now to a 3rd party website")
                .setPositiveButton("Ok") { _, _ ->
                    tvHintPSS.movementMethod = LinkMovementMethod.getInstance()
                }.setNegativeButton("Cancel", null).show()
        }
    }


}


