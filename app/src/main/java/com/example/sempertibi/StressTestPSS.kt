package com.example.sempertibi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.sempertibi.databinding.ActivityStressTestPssBinding


class StressTestPSS : AppCompatActivity() {
    var EXTRA_SCORE: String = "Extra Score"
    lateinit var tvQuestion: TextView
    lateinit var tvQuestionCount: TextView
    lateinit var rbGroup: RadioGroup
    lateinit var rb1: RadioButton
    lateinit var rb2: RadioButton
    lateinit var rb3: RadioButton
    lateinit var rb4: RadioButton
    lateinit var rb5: RadioButton
    lateinit var btnNext: Button
    lateinit var binding: ActivityStressTestPssBinding
    lateinit var currentQuestion: QuestionModel
    var questionCounter: Int = 0
    var questionCountTotal: Int = 10



    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        var score: Int = 0
        val db = this.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null)
        var cursor: Cursor =
            db.rawQuery("SELECT * FROM " + DBContract.QuestionsTable.TABLE_NAME, null)
        var questions = ArrayList<QuestionModel>()
        var id: String
        var question: String
        var answer1: String
        var answer2: String
        var answer3: String
        var answer4: String
        var answer5: String
        var answerNr: String

        try {
            if (cursor.moveToFirst()) {
                do {
                    id =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_ID))
                    question =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_QUESTION))
                    answer1 =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION1))
                    answer2 =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION2))
                    answer3 =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION3))
                    answer4 =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION4))
                    answer5 =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_OPTION5))
                    answerNr =
                        cursor.getString(cursor.getColumnIndex(DBContract.QuestionsTable.COLUMN_ANSWER_NR))
                    questions.add(
                        QuestionModel(
                            id, question,
                            answer1,
                            answer2,
                            answer3,
                            answer4,
                            answer5,
                            answerNr
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }

        super.onCreate(savedInstanceState)
        binding = ActivityStressTestPssBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvQuestion = binding.tvQuestion
        tvQuestionCount = binding.tvQuestionCount
        rbGroup = binding.radioGroup
        rb1 = binding.radioButton1
        rb2 = binding.radioButton2
        rb3 = binding.radioButton3
        rb4 = binding.radioButton4
        rb5 = binding.radioButton5
        btnNext = binding.btNext

        currentQuestion = questions[questionCounter]
        tvQuestion.text = currentQuestion.question
        questionCounter += 1
        tvQuestionCount.text = "Question: " + questionCounter + "/" + questionCountTotal

        rbGroup.clearCheck()
        //TODO("Check Sum")
        var selectedId: Int = rbGroup.checkedRadioButtonId
        if (selectedId == -1) {
        } else{
            var selectedRadioButton: RadioButton = findViewById(selectedId)
            var selectedValue = selectedRadioButton.text
            score += selectedValue.toString().toInt()
        }

        btnNext.setOnClickListener {
            rbGroup.clearCheck()

            if (questionCounter < questionCountTotal) {
                //Toast.makeText(this, "Score: $score", LENGTH_SHORT).show()
                currentQuestion = questions[questionCounter]
                tvQuestion.text = currentQuestion.question
                questionCounter += 1
                tvQuestionCount.text = "Question: " + questionCounter + "/" + questionCountTotal
               // TODO("Check Sum")
                var selectedId: Int = rbGroup.checkedRadioButtonId
                if (selectedId == -1) {
                } else{
                    var selectedRadioButton: RadioButton = findViewById(selectedId)
                    var selectedValue = selectedRadioButton.text
                    score += selectedValue.toString().toInt()
                }
            } else {
            //} else if (questionCountTotal == 10) {
                btnNext.text = "Finish"
                var resultIntent = Intent(this, StressTrackerOverview::class.java)
                startActivity(resultIntent)
                //resultIntent.putExtra(EXTRA_SCORE, score)
                //setResult(RESULT_OK, resultIntent)
            }
        }
    }
}

