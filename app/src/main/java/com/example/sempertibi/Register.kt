package com.example.sempertibi

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    private val passwordPattern: Pattern = Pattern.compile(
        "^" +
                "(?=.*\\d)" +           //at least 1 digit
                //"(?=.*[a-z])" +           //at least 1 lower case letter
                //"(?=.*[A-Z])" +           //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +          //any letter
                "(?=.*[@#$%^&+=])" +        //at least 1 special character
                "(?=\\S+$)" +               //no white spaces
                ".{4,}" +                   //at least 4 characters
                "$"
    )

    lateinit var nestedScrollView: NestedScrollView
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var userInputFieldLayout: TextInputLayout
    private lateinit var userInputFieldText: TextInputEditText
    private lateinit var passwordInputFieldLayout: TextInputLayout
    private lateinit var passwordInputFieldText: TextInputEditText
    private lateinit var passwordRepeatInputFieldLayout: TextInputLayout
    private lateinit var passwordRepeatInputFieldText: TextInputEditText
    private lateinit var emailInputFieldLayout: TextInputLayout
    private lateinit var emailInputFieldText: TextInputEditText
    private lateinit var genderInputField: AutoCompleteTextView
    private lateinit var btnRegister: Button
    private lateinit var appCompatTextViewLoginLink: AppCompatTextView
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
        notificationSwitch = findViewById(R.id.switch1)
        userInputFieldLayout = findViewById(R.id.register_input_username)
        userInputFieldText = findViewById(R.id.usernameInput)
        passwordInputFieldLayout = findViewById(R.id.register_input_password)
        passwordInputFieldText = findViewById(R.id.passwordInput)
        passwordRepeatInputFieldLayout = findViewById(R.id.register_input_repeatPassword)
        passwordRepeatInputFieldText = findViewById(R.id.repeatPasswordInput)
        emailInputFieldLayout = findViewById(R.id.register_input_email)
        emailInputFieldText = findViewById(R.id.emailInput)
        genderInputField = findViewById(R.id.input_gender)
        btnRegister = findViewById(R.id.btRegister)
        appCompatTextViewLoginLink = findViewById(R.id.appCompatTextViewLoginLink)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        timer = Timer()

        var notificationSetting = true

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                // If the notification is set false, user should get alert dialogue
                AlertDialog.Builder(this@Register).setTitle("Notification")
                    .setMessage("Please enable notifications to get a daily reminder for the app to be used")
                    .setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(
                            applicationContext,
                            "Notifications disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setPositiveButton("Accept") { _, _ ->
                        requestPermission()
                        notificationSwitch.isChecked = true
                    }.show()
            }
            notificationSetting = isChecked
        }

        btnRegister.setOnClickListener {
            if (!hasNotificationPermission()) {
                requestPermission()
            } else {
                scheduleNotify()
                lifecycleScope.launch {

                    // Instead of PBKDF2 -> BCrypt used
                    val saltValue = BCrypt.gensalt()

                    val insertUser = listOf(
                        User(
                            user_id = 0,
                            name = userInputFieldText.text.toString(),
                            passwordHash = BCrypt.hashpw(
                                passwordInputFieldText.text.toString(),
                                saltValue
                            ),
                            salt = saltValue,
                            gender = genderInputField.text.toString(),
                            email = emailInputFieldText.text.toString(),
                            notification = notificationSetting,
                        )
                    )

                    val existingEntry =
                        withContext(Dispatchers.IO) { dao.getUserByMail(emailInputFieldText.text.toString()) }

                    if (existingEntry == null) {
                        // implement checks on the input data
                        if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                            lifecycleScope.launch {
                                insertUser.forEach { dao.addUser(it) }
                            }
                            // Insert user id for security questions
                            lifecycleScope.launch {
                                GlobalData.userID = dao.getUserByMail(emailInputFieldText.text.toString())?.user_id
                            }

                            emptyInputEditText()

                            Toast.makeText(
                                applicationContext,
                                "Security questions following",
                                Toast.LENGTH_SHORT
                            ).show()

                            btnRegister.visibility = View.GONE
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    val intent = Intent(this@Register, SecurityQuestions::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }, 1000)
                        } else {
                            validateUsername()
                            validateEmail()
                            validatePassword()
                            validateRepeatedPassword()
                        }
                    } else {
                        // If there is an entry for this email address, then user is notified
                        AlertDialog.Builder(this@Register).setTitle("E-Mail found")
                            .setMessage("There is already an entry for this E-Mail address. Please use another email address or reset password")
                            .setPositiveButton("Reset Password") { _, _ ->
                                startActivity(Intent(this@Register, ForgotPassword::class.java))
                            }
                            .setNegativeButton("Registration"){_,_->
                                Toast.makeText(
                                    applicationContext,
                                    "Please use another email address",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@Register, Register::class.java))
                            }
                            .show()
                    }
                }
            }
        }

        appCompatTextViewLoginLink.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }
    }


    /*
    Validates the Username input in Registration process
     */
    private fun validateUsername(): Boolean {
        val userInput = userInputFieldLayout.editText?.text.toString().trim()
        return if (userInput.isEmpty()) {
            userInputFieldLayout.error = "Field can't be empty"
            false
        } else if (userInput.length > 15) {
            userInputFieldLayout.error = "Username too long"
            false
        } else {
            userInputFieldLayout.error = null
            true
        }
    }

    /*
    Validates the Password input in Registration process
     */
    private fun validatePassword(): Boolean {
        val passwordInput = passwordInputFieldLayout.editText?.text.toString().trim()
        return if (passwordInput.isEmpty()) {
            passwordInputFieldLayout.error = "Field can't be empty"
            false
        } else if (!passwordPattern.matcher(passwordInput).matches()) {
            passwordInputFieldLayout.error =
                "Password too weak - min. 4 characters! Use upper and lowercase letters, numbers, and special symbols like @#\$%^&+="
            false
        } else {
            passwordInputFieldLayout.error = null
            true
        }
    }

    /*
    Validates the repeated Password input in Registration process
     */
    private fun validateRepeatedPassword(): Boolean {
        val passwordInput = passwordInputFieldLayout.editText?.text.toString().trim()
        val repeatedPasswordInput = passwordRepeatInputFieldLayout.editText?.text.toString().trim()
        return if (repeatedPasswordInput.isEmpty()) {
            passwordRepeatInputFieldLayout.error = "Field can't be empty"
            false
        } else if (passwordInput != repeatedPasswordInput) {
            passwordRepeatInputFieldLayout.error = "Passwords must be equal"
            false
        } else {
            passwordRepeatInputFieldLayout.error = null
            true
        }
    }

    /*
    Validates the Email input in Registration process
     */
    private fun validateEmail(): Boolean {
        val emailInput = emailInputFieldLayout.editText?.text.toString().trim()
        return if (emailInput.isEmpty()) {
            emailInputFieldLayout.error = "Field can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailInputFieldLayout.error = "Please enter a valid email address"
            false
        } else {
            emailInputFieldLayout.error = null
            true
        }
    }

    /*
     This method is to empty all input edit text
    */
    private fun emptyInputEditText() {
        userInputFieldText.text = null
        emailInputFieldText.text = null
        passwordInputFieldText.text = null
        passwordRepeatInputFieldText.text = null
        genderInputField.text = null
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1
    }

    private fun scheduleNotify() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(this, AlarmReceiver::class.java)
        notificationIntent.putExtra("message", "Please use the app SemperTibi today")
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            FLAG_IMMUTABLE
        )

        // Set the time to trigger the alarm
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 13) // Set the hour of the day (24-hour clock)
            set(Calendar.MINUTE, 50) // Set the minute of the hour
            set(Calendar.SECOND, 0) // Set the second of the minute
        }

        // Set the alarm to trigger at the specified time
        val triggerTime = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

        // Set the alarm to repeat every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}