package com.example.sempertibi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.SecurityQuestion
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsSecurityQuestions : AppCompatActivity() {

    private lateinit var inputLayout1: TextInputLayout
    private lateinit var input1: TextInputEditText
    private lateinit var inputLayout2: TextInputLayout
    private lateinit var input2: TextInputEditText
    private lateinit var inputLayout3: TextInputLayout
    private lateinit var input3: TextInputEditText
    private lateinit var inputLayout4: TextInputLayout
    private lateinit var input4: TextInputEditText
    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var tv3: TextView
    private lateinit var tv4: TextView
    private lateinit var saveButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_security_questions)
        StrictMode.enableDefaults()

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
        initializeViews()

        lifecycleScope.launch {
            val userQuestions = withContext(Dispatchers.IO){dao.getSecurityQuestions(GlobalData.userID!!)}

            tv1.text = userQuestions[0].question_text.trim()
            tv2.text = userQuestions[1].question_text.trim()
            tv3.text = userQuestions[2].question_text.trim()
            tv4.text = userQuestions[3].question_text.trim()

            input1.setText(userQuestions[0].answer.trim())
            input2.setText(userQuestions[1].answer.trim())
            input3.setText(userQuestions[2].answer.trim())
            input4.setText(userQuestions[3].answer.trim())

            saveButton.setOnClickListener {
                if (validateQuestion1() and validateQuestion2() and validateQuestion3() and validateQuestion4()) {
                    val insertQuestions = listOf(
                        SecurityQuestion(
                            question_id = userQuestions[0].question_id,
                            user_id = userQuestions[0].user_id,
                            question_text = userQuestions[0].question_text.trim(),
                            answer = input1.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = userQuestions[1].question_id,
                            user_id = userQuestions[1].user_id,
                            question_text = userQuestions[1].question_text.trim(),
                            answer = input2.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = userQuestions[2].question_id,
                            user_id = userQuestions[2].user_id,
                            question_text = userQuestions[2].question_text.trim(),
                            answer = input3.text.toString()
                        ),
                        SecurityQuestion(
                            question_id = userQuestions[3].question_id,
                            user_id = userQuestions[3].user_id,
                            question_text = userQuestions[3].question_text.trim(),
                            answer = input4.text.toString()
                        )
                    )

                    lifecycleScope.launch {
                        insertQuestions.forEach { withContext(Dispatchers.IO){dao.updateSecurityQuestions(it) }}
                    }

                    // Toast to show success message that record saved successfully
                    Toast.makeText(
                        applicationContext,
                        "Answers saved",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    validateQuestion1()
                    validateQuestion2()
                    validateQuestion3()
                    validateQuestion4()
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuDashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuMoodJournal -> {
                    val intent = Intent(this, MoodJournalOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuStressTracker -> {
                    val intent = Intent(this, StressTrackerOverview::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuToDoList -> {
                    val intent = Intent(this, ToDoList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menuSignOut -> {
                    AlertDialog.Builder(this).setTitle("Sign Out")
                        .setMessage("Do you really want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
                            // Handle sign out here
                            val myApplication = applicationContext as MyApplication
                            myApplication.clearGlobalData()
                            val packageManager = applicationContext.packageManager
                            val intent =
                                packageManager.getLaunchIntentForPackage(applicationContext.packageName)
                            val componentName = intent!!.component
                            val mainIntent = Intent.makeRestartActivityTask(componentName)
                            applicationContext.startActivity(mainIntent)
                        }
                        .setNegativeButton("No") { _, _ ->
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeViews() {
        inputLayout1 = findViewById(R.id.streetNameInputLayout)
        input1 = findViewById(R.id.streetNameInput)
        inputLayout2 = findViewById(R.id.motherNameInputLayout)
        input2 = findViewById(R.id.motherNameInput)
        inputLayout3 = findViewById(R.id.movieNameInputLayout)
        input3 = findViewById(R.id.movieNameInput)
        inputLayout4 = findViewById(R.id.colorInputLayout)
        input4 = findViewById(R.id.colorInput)
        saveButton = findViewById(R.id.btSaveSecurityQuestions)
        bottomNavigationView = findViewById(R.id.bottom_nav)
        tv1 = findViewById(R.id.tvQuestion1)
        tv2 = findViewById(R.id.tvQuestion2)
        tv3 = findViewById(R.id.tvQuestion3)
        tv4 = findViewById(R.id.tvQuestion4)
    }

    private fun validateQuestion1(): Boolean {
        val userInput = inputLayout1.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            inputLayout1.error = "Field can't be empty"
            false
        } else {
            inputLayout1.error = null
            true
        }
    }

    private fun validateQuestion2(): Boolean {
        val userInput = inputLayout2.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            inputLayout2.error = "Field can't be empty"
            false
        } else {
            inputLayout2.error = null
            true
        }
    }

    private fun validateQuestion3(): Boolean {
        val userInput = inputLayout3.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            inputLayout3.error = "Field can't be empty"
            false
        } else {
            inputLayout3.error = null
            true
        }
    }

    private fun validateQuestion4(): Boolean {
        val userInput = inputLayout4.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            inputLayout4.error = "Field can't be empty"
            false
        } else {
            inputLayout4.error = null
            true
        }
    }
}