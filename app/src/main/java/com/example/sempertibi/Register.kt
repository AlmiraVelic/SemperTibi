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
import android.os.StrictMode
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    private val passwordPattern: Pattern = Pattern.compile(
        "^" +
                "(?=.*\\d)" +           //at least 1 digit
                //"(?=.*[a-z])" +           //at least 1 lower case letter
                //"(?=.*[A-Z])" +           //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +          //any letter
                "(?=.*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?])" +        //at least 1 special character
                "(?=\\S+$)" +               //no white spaces
                ".{10,}" +                   //at least 10 characters
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
        StrictMode.enableDefaults()

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
                        AlertDialog.Builder(this@Register).setTitle("Notification info")
                            .setMessage("A notification to use the app daily is needed for scientific purpose and will be sent daily at approximately 2 PM")
                            .setPositiveButton("OK", null)
                            .show()
                        requestPermission()
                        notificationSwitch.isChecked = true
                    }.show()
            }
            notificationSetting = isChecked
        }

        btnRegister.setOnClickListener {
            if (!hasNotificationPermission()) {
                AlertDialog.Builder(this@Register).setTitle("Notification info")
                    .setMessage("A notification to use the app daily is needed for scientific purpose and will be sent daily at approximately 2 PM")
                    .setPositiveButton("OK", null)
                    .show()
                requestPermission()
            } else {
                scheduleNotify()
                lifecycleScope.launch {

                    // Instead of PBKDF2 -> BCrypt used
                    val name = userInputFieldText.text.toString()
                    val saltValue = BCrypt.gensalt()
                    val passwordHash = BCrypt.hashpw(
                        passwordInputFieldText.text.toString(),
                        saltValue
                    )
                    val gender = genderInputField.text.toString()
                    val email =
                        emailInputFieldText.text.toString()

                    val insertUser = listOf(
                        User(
                            user_id = 0,
                            name,
                            passwordHash,
                            saltValue,
                            gender,
                            email,
                            notificationSetting
                        )
                    )

                    // url to post our data
                    val url = "http://192.168.0.192/sempertibi/insert_user.php"

                    // creating a new variable for our request queue
                    val requestQueue = Volley.newRequestQueue(this@Register)

                    val parameters = JSONObject().apply {
                        put("name", name)
                        put("passwordHash", passwordHash)
                        put("salt", saltValue)
                        put("gender", gender)
                        put("email", email)
                        put(
                            "notification", if (notificationSetting) {
                                1
                            } else {
                                0
                            }
                        )
                    }

                    val existingEntry =
                        withContext(Dispatchers.IO) { dao.getUserByMail(email) }
                    val existingEntryName =
                        withContext(Dispatchers.IO) { dao.getUserByUsername(name) }

                    if (existingEntryName != null) {
                        // User is found on local db, do something else
                        // If there is an entry for this email address, then user is notified
                        AlertDialog.Builder(this@Register).setTitle("Username found")
                            .setMessage("There is already a user created with this username. Please chose another username")
                            .setPositiveButton("Registration") { _, _ ->
                                Toast.makeText(
                                    applicationContext,
                                    "Please use another username",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@Register,
                                        Register::class.java
                                    )
                                )
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                                startActivity(Intent(this@Register, SigninActivity::class.java))
                            }
                            .show()
                    } else if ((!checkUser(email)) && (existingEntry == null)) {
                        // User not found in local or server db
                        if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                            // If there is an active network connection, make an HTTP request to the PHP script
                            val htmlRequest = HtmlRequest(
                                Request.Method.POST, url, parameters.toString(),
                                { _ ->
                                    // Handle the response from the server
                                    Toast.makeText(
                                        applicationContext,
                                        "New user created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("mysql", "user created")
                                },
                                { error ->
                                    // Handle the error
                                    Toast.makeText(
                                        applicationContext,
                                        "Error: " + error.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("mysql", error.message.toString())
                                    Log.e("mysql", "Error: ${error.message}", error)
                                })
                            Log.e("mysql", parameters.toString())

                            // Add the request to the request queue to Server
                            requestQueue.add(htmlRequest)

                            // Add registration to room db locally
                            lifecycleScope.launch {
                                insertUser.forEach { dao.addUser(it) }
                            }

                            GlobalData.emailUser = emailInputFieldText.text.toString()

                            emptyInputEditText()

                            Toast.makeText(
                                applicationContext,
                                "Security questions following",
                                Toast.LENGTH_SHORT
                            ).show()

                            btnRegister.visibility = View.GONE

                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    val intent =
                                        Intent(this@Register, SecurityQuestions::class.java)
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
                    } else if ((checkUser(email)) && (existingEntry == null)) {
                        // If mySQL Db shows a user, but locally no user
                        if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                            // Add registration to room db locally
                            lifecycleScope.launch {
                                insertUser.forEach { dao.addUser(it) }
                            }

                            GlobalData.emailUser = emailInputFieldText.text.toString()

                            emptyInputEditText()

                            Toast.makeText(
                                applicationContext,
                                "Security questions following",
                                Toast.LENGTH_SHORT
                            ).show()

                            btnRegister.visibility = View.GONE
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    val intent =
                                        Intent(this@Register, SecurityQuestions::class.java)
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
                    } else if ((!checkUser(email)) && (existingEntry != null)) {
                        if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                            // If in MySQL Db user is not found, but locally found
                            val htmlRequest = HtmlRequest(
                                Request.Method.POST, url, parameters.toString(),
                                { _ ->
                                    // Handle the response from the server
                                    Toast.makeText(
                                        applicationContext,
                                        "New user created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("mysql", "user created in mysql")
                                },
                                { error ->
                                    // Handle the error
                                    Toast.makeText(
                                        applicationContext,
                                        "Error: " + error.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("mysql", error.message.toString())

                                })
                            Log.e("mysql", parameters.toString())
                            // Add the request to the request queue to Server
                            requestQueue.add(htmlRequest)

                            // update registration info (hash and salt value) to room db locally
                            lifecycleScope.launch {
                                insertUser.forEach { dao.updateUser(it) }
                            }

                            GlobalData.emailUser = emailInputFieldText.text.toString()

                            emptyInputEditText()

                            Toast.makeText(
                                applicationContext,
                                "Security questions following",
                                Toast.LENGTH_SHORT
                            ).show()

                            btnRegister.visibility = View.GONE
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    val intent =
                                        Intent(this@Register, SecurityQuestions::class.java)
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
                    } else if ((checkUser(email)) && (existingEntry != null)) {
                        // User is found, do something else
                        // If there is an entry for this email address, then user is notified
                        AlertDialog.Builder(this@Register).setTitle("E-Mail found")
                            .setMessage("There is already an entry for this E-Mail address. Please use another email address or reset password")
                            .setPositiveButton("Reset Password") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@Register,
                                        ForgotPassword::class.java
                                    )
                                )
                            }
                            .setNegativeButton("Registration") { _, _ ->
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
                "Password too weak - min. 10 characters! Use upper and lowercase letters, numbers, and special characters."
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
        notificationIntent.putExtra("message", "Please use the SemperTibi app today")
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            FLAG_IMMUTABLE
        )

        // Set the time to trigger the alarm
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14) // Set the hour of the day (24-hour clock)
            set(Calendar.MINUTE, 0) // Set the minute of the hour
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

    private fun checkUser(email: String): Boolean {
        // Make a request to the PHP script and parse the response as JSON
        val url = URL("http://192.168.0.192/sempertibi/getuserbymail.php")

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true

        val writer = OutputStreamWriter(conn.outputStream)
        writer.write("email=$email")
        writer.flush()

        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val response = reader.readLine()
        reader.close()

        var result = false
        if (response != null) {
            try {
                val json = JSONObject(response)
                result = json.optBoolean("user_found", false)
            } catch (e: JSONException) {
                // Handle non-JSON response here
                Log.e("checkUser", "Error parsing JSON: $response")
            }
        }

        return result
    }
}