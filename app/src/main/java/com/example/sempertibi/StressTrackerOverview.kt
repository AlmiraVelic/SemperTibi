package com.example.sempertibi

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sempertibi.databinding.ActivityStressTrackerOverviewBinding

class StressTrackerOverview : AppCompatActivity() {
    private lateinit var binding: ActivityStressTrackerOverviewBinding
    lateinit var quizDBHelper: QuizDbHelper
    private var REQUEST_CODE_QUIZ = 1
    public var SHARED_PREFS: String = "sharedPrefs"
    public var KEY_SCORE: String = "keyScore"

    lateinit var tvStressLevel: TextView
    lateinit var tvStressOutput: TextView
    lateinit var seekBar: SeekBar

    private var stressScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStressTrackerOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        quizDBHelper = QuizDbHelper(this)

        //loadStressScore()

        val newMeasureHRV = binding.btnNewMeasureHRV
        newMeasureHRV.setOnClickListener {
            var intent = Intent(this, StressTestHRV::class.java)
            startActivity(intent)
        }

        val learnMore = binding.btnLearnMore
        learnMore.setOnClickListener {
            var intent = Intent(this, StressTrackerLearn::class.java)
            startActivity(intent)
        }

        val icon = binding.logo
        icon.bringToFront()

        val scrollView = binding.nestedScrollView
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }

    fun addQuestionsToTable(view: View) {
        var answer1 = getString(R.string.answerPSS1)
        var answer2 = getString(R.string.answerPSS2)
        var answer3 = getString(R.string.answerPSS3)
        var answer4 = getString(R.string.answerPSS4)
        var answer5 = getString(R.string.answerPSS5)
        quizDBHelper.addQuestion(QuestionModel(id = "1", question = getString(R.string.questionPSS1), option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("2",question = getString(R.string.questionPSS2),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("3",question = getString(R.string.questionPSS3),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("4",question = getString(R.string.questionPSS4),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("5",question = getString(R.string.questionPSS5),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("6",question = getString(R.string.questionPSS6),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("7",question = getString(R.string.questionPSS7),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("8",question = getString(R.string.questionPSS8),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("9",question = getString(R.string.questionPSS9),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))
        quizDBHelper.addQuestion(QuestionModel("10",question = getString(R.string.questionPSS10),option1 = answer1, option2 = answer2, option3 = answer3, option4 = answer4, option5 = answer5, answerNr = "1"))

        startActivity(Intent(this, StressTestPSS::class.java))

        setTVs()
        // Caller
        //var intent = Intent(this, StressTestPSS::class.java)
        //getResult.launch(intent)
    }

    fun setTVs(){
        tvStressOutput.text = getString(R.string.outputPSS2)
        seekBar.progress = 16
    }

    //Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                var extraScore = StressTestPSS()
                val score = it.data?.getIntExtra(extraScore.EXTRA_SCORE, 0)
                if (score != null) {
                    if (score > stressScore) {
                        updateStressScore(score)
                    }
                }
            }
        }

    private fun loadStressScore(){
        val prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        stressScore = prefs.getInt(KEY_SCORE,0)
        tvStressLevel.text = "Your Stress Level: $stressScore"
    }


    private fun updateStressScore(stressScoreNew: Int) {
        stressScore = stressScoreNew
        tvStressLevel.text = "Your Stress Level: $stressScore"

        val prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_SCORE, stressScore)
        editor.apply()

    }


}
