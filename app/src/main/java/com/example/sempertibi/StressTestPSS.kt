package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.StressPSS
import kotlinx.coroutines.launch
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
    private lateinit var btnSave: Button

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

        rbGroup = findViewById(R.id.radioGroup)
        rb1 = findViewById(R.id.radio_button_1)
        rb2 = findViewById(R.id.radio_button_2)
        rb3 = findViewById(R.id.radio_button_3)
        rb4 = findViewById(R.id.radio_button_4)
        rb5 = findViewById(R.id.radio_button_5)
        informationText = findViewById(R.id.informationText)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        btnNext = findViewById(R.id.btNext)
        btnSave = findViewById(R.id.btSave)

        btnNext.setOnClickListener(this)
        setQuestion(0)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btNext -> {
                val checkedButton =
                    rbGroup.findViewById<RadioButton>(rbGroup.checkedRadioButtonId)
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

                    // If user wants to restart the test for today
                    btnNext.text = getString(R.string.restartTest)
                    btnNext.setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to restart the test?")
                            .setPositiveButton("Yes") { _, _ ->
                                // User confirmed
                                // Reset the activity if the button text is "Restart Test"
                                startActivity(Intent(this, StressTestPSS::class.java))
                                finish()
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                    btnSave.setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Do you want to save the test data?")
                            .setPositiveButton("Yes") { _, _ ->
                                // User confirmed
                                val dao = UserDatabase.getInstance(this).userDao()
                                val stressPSS = StressPSS(
                                    testPSS_id = 0,
                                    user_id = GlobalData.userID!!,
                                    testPSS_date = Date(),
                                    PSS_score = GlobalData.pssScore!!
                                )

                                lifecycleScope.launch {
                                    dao.addStressPSS(stressPSS)
                                }

                                Toast.makeText(applicationContext, "Results saved", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, StressTrackerOverview::class.java))
                                finish()
                            }
                            .setNegativeButton("No"){ _, _ ->
                                startActivity(Intent(this, StressTrackerOverview::class.java))
                                finish()
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun setQuestion(index: Int) {
        tvQuestion.text = questions[index]
        var myindex = index+1
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
            0, 1 -> {
                pssScore += 1
            }
            2, 3 -> {
                pssScore += 2
            }
            4 -> {
                pssScore += 3
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

        // Display the PSS score
        tvQuestion.text = "Your PSS score is $pssScore \n \n$response"
        // Not needed Views and rbGroup set invisible
        tvQuestionCount.visibility = View.GONE
        rbGroup.visibility = View.GONE
        informationText.visibility = View.GONE
        // For saving the results a button is set visible
        btnSave.visibility = View.VISIBLE
    }
}
