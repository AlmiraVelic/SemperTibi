package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.SecurityQuestion
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import java.util.*

class SecurityQuestions : AppCompatActivity() {

    private lateinit var streetNameInputLayout: TextInputLayout
    private lateinit var streetNameInput: TextInputEditText
    private lateinit var motherNameInputLayout: TextInputLayout
    private lateinit var motherNameInput: TextInputEditText
    private lateinit var movieNameInputLayout: TextInputLayout
    private lateinit var movieNameInput: TextInputEditText
    private lateinit var colorInputLayout: TextInputLayout
    private lateinit var colorInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_questions)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
        initializeViews()
        timer = Timer()

        saveButton.setOnClickListener {
            if (validateQuestion1() and validateQuestion2() and validateQuestion3() and validateQuestion4()) {
                val insertQuestions = listOf(
                    SecurityQuestion(
                        question_id = 0,
                        user_id = GlobalData.userID!!,
                        question_text = R.string.security_question_1.toString(),
                        answer = streetNameInput.text.toString()
                    ),
                    SecurityQuestion(
                        question_id = 0,
                        user_id = GlobalData.userID!!,
                        question_text = R.string.security_question_2.toString(),
                        answer = motherNameInput.text.toString()
                    ),
                    SecurityQuestion(
                        question_id = 0,
                        user_id = GlobalData.userID!!,
                        question_text = R.string.security_question_3.toString(),
                        answer = movieNameInput.text.toString()
                    ),
                    SecurityQuestion(
                        question_id = 0,
                        user_id = GlobalData.userID!!,
                        question_text = R.string.security_question_4.toString(),
                        answer = colorInput.text.toString()
                    )
                )

                lifecycleScope.launch {
                    insertQuestions.forEach { dao.insertSecurityQuestions(it) }
                }

                emptyInputEditText()

                saveButton.visibility = View.GONE

                // Toast to show success message that record saved successfully
                Toast.makeText(
                    applicationContext,
                    getString(R.string.success_message),
                    Toast.LENGTH_SHORT
                ).show()
                lifecycleScope.launch {
                    val existingEntry =
                        withContext(Dispatchers.IO) { dao.getUserByID(GlobalData.userID!!) }

                    val user = existingEntry?.name
                    val mailMessage =
                        "<h1>Welcome to Semper Tibi</h1>" +
                                "<p>Dear $user," +
                                "<br><br>" +
                                "This E-Mail is automatically generated after registration to the Semper Tibi Android App." +
                                "<br><br>" +
                                "Enjoy the usage of <b>SemperTibi!</b>" +
                                "</p>" +
                                "<br><br>" +
                                "<p>Please send an e-mail to us, in case you have not registered by yourself!<br>" +
                                "<a href=\"sempertibi.app@gmail.com\">sempertibi.app@gmail.com</a>" +
                                "</p>"
                    if (user != null) {
                        sendEmail(existingEntry.email, user, mailMessage)
                    }
                }

                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val intent = Intent(this@SecurityQuestions, SigninActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)
            } else {
                validateQuestion1()
                validateQuestion2()
                validateQuestion3()
                validateQuestion4()
            }
        }
    }

    private fun initializeViews() {
        streetNameInputLayout = findViewById(R.id.streetNameInputLayout)
        streetNameInput = findViewById(R.id.streetNameInput)
        motherNameInputLayout = findViewById(R.id.motherNameInputLayout)
        motherNameInput = findViewById(R.id.motherNameInput)
        movieNameInputLayout = findViewById(R.id.movieNameInputLayout)
        movieNameInput = findViewById(R.id.movieNameInput)
        colorInputLayout = findViewById(R.id.colorInputLayout)
        colorInput = findViewById(R.id.colorInput)
        saveButton = findViewById(R.id.btSaveSecurityQuestions)
    }

    private fun validateQuestion1(): Boolean {
        val userInput = streetNameInputLayout.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            streetNameInputLayout.error = "Field can't be empty"
            false
        } else {
            streetNameInputLayout.error = null
            true
        }
    }

    private fun validateQuestion2(): Boolean {
        val userInput = motherNameInputLayout.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            motherNameInputLayout.error = "Field can't be empty"
            false
        } else {
            motherNameInputLayout.error = null
            true
        }
    }

    private fun validateQuestion3(): Boolean {
        val userInput = movieNameInputLayout.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            movieNameInputLayout.error = "Field can't be empty"
            false
        } else {
            movieNameInputLayout.error = null
            true
        }
    }

    private fun validateQuestion4(): Boolean {
        val userInput = colorInputLayout.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            colorInputLayout.error = "Field can't be empty"
            false
        } else {
            colorInputLayout.error = null
            true
        }
    }

    private fun emptyInputEditText() {
        streetNameInput.text = null
        motherNameInput.text = null
        movieNameInput.text = null
        colorInput.text = null
    }


    /**
     * Sends Mails to Users saved mail address
     */
    private fun sendEmail(email: String, name: String, message: String) {
        val sendgridAPIKey = BuildConfig.API_KEY
        val sendGrid = SendGrid.create(sendgridAPIKey)

        val mail = SendGridMail()
        mail.addRecipient(email, name)
        mail.setFrom("sempertibi.app@gmail.com", "SemperTibi App")
        mail.setSubject("Password Reset for SemperTibi")
        mail.setHtmlContent(message)

        val task = SendTask(sendGrid)
        task.send(mail)
    }
}