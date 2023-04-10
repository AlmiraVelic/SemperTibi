package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecurityCheck : AppCompatActivity() {

    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var inputLayout1: TextInputLayout
    private lateinit var input1: TextInputEditText
    private lateinit var inputLayout2: TextInputLayout
    private lateinit var input2: TextInputEditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_check)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
        initializeViews()

        val userID: Int = GlobalData.userID!!

        lifecycleScope.launch {
            val twoRandomQuestions = withContext(Dispatchers.IO) {dao.getTwoRandomSecurityQuestions(
                userID
            )}

            tv1.text = twoRandomQuestions[0].question_text
            tv2.text = twoRandomQuestions[1].question_text

            saveButton.setOnClickListener {

                val answer1 = input1.text.toString().trim()
                val answer2 = input2.text.toString().trim()

                if (validateAnswer1() && validateAnswer2()) {
                    if (answer1 == twoRandomQuestions[0].answer.trim() && answer2 == twoRandomQuestions[1].answer.trim()) {
                        Toast.makeText(
                            applicationContext,
                            "Answers are correct",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Authentication succeeded
                        // set Global Data for Settings Activity
                        lifecycleScope.launch {
                                val userInformation = withContext(Dispatchers.IO) {dao.getUserByID(userID)}
                                GlobalData.emailUser = userInformation!!.email
                                GlobalData.notificationUser = userInformation.notification
                                GlobalData.genderUser = userInformation.gender
                        }

                        startActivity(Intent(this@SecurityCheck, Dashboard::class.java))
                    }else{
                        Toast.makeText(
                            applicationContext,
                            "Answers are incorrect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    validateAnswer1()
                    validateAnswer2()
                }
            }
        }

    }

    private fun initializeViews() {
        tv1 = findViewById(R.id.tvQuestion1)
        tv2 = findViewById(R.id.tvQuestion2)
        inputLayout1 = findViewById(R.id.inputLayout1)
        input1 = findViewById(R.id.input1)
        inputLayout2 = findViewById(R.id.inputLayout2)
        input2 = findViewById(R.id.input2)
        saveButton = findViewById(R.id.btSaveSecurityQuestions)
    }

    private fun validateAnswer1(): Boolean{
        val answer1 =  inputLayout1.editText?.text.toString().trim()
        return if (answer1.isEmpty()) {
            inputLayout1.error = "Field can't be empty"
            false
        } else {
            inputLayout1.error = null
            true
        }
    }

    private fun validateAnswer2(): Boolean{
        val answer2 =  inputLayout2.editText?.text.toString().trim()
        return if (answer2.isEmpty()) {
            inputLayout2.error = "Field can't be empty"
            false
        } else {
            inputLayout2.error = null
            true
        }
    }
}