package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.SecurityQuestion
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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
        StrictMode.enableDefaults()

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
        initializeViews()
        timer = Timer()

        val email = GlobalData.emailUser.toString()
        lifecycleScope.launch {

            val userDB = withContext(Dispatchers.IO) { dao.getUserByMail(email) }
            val userID = userDB!!.user_id

            val userIDServer = getUserID(email)
            Log.d("mysql", "userID set")

            saveButton.setOnClickListener {
                if (validateQuestion1() and validateQuestion2() and validateQuestion3() and validateQuestion4()) {

                    // server db
                    insertSecurityQuestion(
                        userIDServer,
                        getString(R.string.security_question_1),
                        streetNameInput.text.toString()
                    )
                    insertSecurityQuestion(
                        userIDServer,
                        getString(R.string.security_question_2),
                        motherNameInput.text.toString()
                    )
                    insertSecurityQuestion(
                        userIDServer,
                        getString(R.string.security_question_3),
                        movieNameInput.text.toString()
                    )
                    insertSecurityQuestion(
                        userIDServer,
                        getString(R.string.security_question_4),
                        colorInput.text.toString()
                    )

                    Log.d("mysql", "questions inserted")

                    // local db
                    val insertQuestions = listOf(
                        SecurityQuestion(
                            question_id = 0,
                            userID,
                            question_text = getString(R.string.security_question_1),
                            answer = streetNameInput.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = 0,
                            userID,
                            question_text = getString(R.string.security_question_2),
                            answer = motherNameInput.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = 0,
                            userID,
                            question_text = getString(R.string.security_question_3),
                            answer = movieNameInput.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = 0,
                            userID,
                            question_text = getString(R.string.security_question_4),
                            answer = colorInput.text.toString()
                        )
                    )

                    lifecycleScope.launch {
                        insertQuestions.forEach { dao.insertSecurityQuestions(it) }
                    }

                    Toast.makeText(
                        applicationContext,
                        "Security question inserted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

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
                            withContext(Dispatchers.IO) { dao.getUserByID(userID) }

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
        mail.setSubject("Registration in SemperTibi")
        mail.setHtmlContent(message)

        val task = SendTask(sendGrid)
        task.send(mail)
    }

    private fun getUserID(email: String): Int {
        val url = URL("http://192.168.0.192/sempertibi/getuserIDbymail.php")

        Log.d("mysql", "1")

        val conn = url.openConnection() as HttpURLConnection
        Log.d("mysql", "2")
        conn.requestMethod = "POST"
        Log.d("mysql", "3")
        conn.doOutput = true
        Log.d("mysql", "4")

        val writer = OutputStreamWriter(conn.outputStream)
        Log.d("mysql", "5")
        writer.write("email=${URLEncoder.encode(email, "UTF-8")}")
        Log.d("mysql", "6")
        writer.flush()
        Log.d("mysql", "7")

        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        Log.d("mysql", "8")
        val response = reader.readLine()
        Log.d("mysql", "9")
        reader.close()
        Log.d("mysql", "10")

        conn.disconnect()
        Log.d("mysql", "11")

        val jsonObject = JSONObject(response)
        Log.d("mysql", "12")
        if (jsonObject.has("error")) {
            Log.d("mysql", "13")
            throw RuntimeException(jsonObject.getString("error"))
        } else {
            Log.d("mysql", "14")
            return jsonObject.getInt("user_id")
        }
    }

    private fun insertSecurityQuestion(userId: Int, questionText: String, answer: String) {
        val url = "http://192.168.0.192/sempertibi/insert_questions.php"
        val request = object : StringRequest(
            Method.POST,
            url,
            { _ ->
                // Handle the response from the server
            },
            { error ->
                // Handle the error
                Toast.makeText(
                    applicationContext,
                    "Error: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "user_id" to userId.toString(),
                    "question_text" to questionText,
                    "answer" to answer
                )
            }
        }

        // Add the request to the request queue
        Volley.newRequestQueue(this@SecurityQuestions).add(request)
    }


}