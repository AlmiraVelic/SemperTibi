package com.example.sempertibi

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.regex.Pattern

class Settings : AppCompatActivity() {

    private val passwordPattern: Pattern = Pattern.compile(
        "^" +
                "(?=.*\\d)" +               //at least 1 digit
                //"(?=.*[a-z])" +           //at least 1 lower case letter
                //"(?=.*[A-Z])" +           //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +          //any letter
                "(?=.*[@#$%^&+=])" +        //at least 1 special character
                "(?=\\S+$)" +               //no white spaces
                ".{4,}" +                   //at least 4 characters
                "$"
    )

    lateinit var notificationSwitch: SwitchMaterial

    lateinit var userInputFieldLayout: TextInputLayout
    lateinit var passwordInputFieldLayout: TextInputLayout
    lateinit var passwordRepeatInputFieldLayout: TextInputLayout
    lateinit var emailInputFieldLayout: TextInputLayout
    lateinit var genderInputLayout: TextInputLayout

    lateinit var usernameInputEditText: TextInputEditText
    lateinit var passwordInputEditText: TextInputEditText
    lateinit var passwordRepeatEditText: TextInputEditText
    lateinit var emailInputEditText: TextInputEditText
    lateinit var genderInputField: AutoCompleteTextView
    lateinit var btnSaveChanges: Button

    lateinit var tvChangeSecurityQuestions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val dao = UserDatabase.getInstance(this).userDao()

        userInputFieldLayout = findViewById(R.id.username)
        passwordInputFieldLayout = findViewById(R.id.password)
        passwordRepeatInputFieldLayout = findViewById(R.id.repeatPassword)
        emailInputFieldLayout = findViewById(R.id.email)
        notificationSwitch = findViewById(R.id.switch1)
        usernameInputEditText = findViewById(R.id.usernameInput)
        passwordInputEditText = findViewById(R.id.passwordInput)
        passwordRepeatEditText = findViewById(R.id.repeatPasswordInput)
        emailInputEditText = findViewById(R.id.emailInput)
        genderInputLayout = findViewById(R.id.setGender)
        genderInputField = findViewById(R.id.input_gender)
        btnSaveChanges = findViewById(R.id.btSave)
        tvChangeSecurityQuestions = findViewById(R.id.tvChangeSecurityQuestions)

        notificationSwitch.isChecked = GlobalData.notificationUser ?: false
        usernameInputEditText.setText(GlobalData.loggedInUser)
        passwordInputEditText.setText(GlobalData.passwordUser)
        passwordRepeatEditText.setText(GlobalData.passwordUser)
        emailInputEditText.setText(GlobalData.emailUser)
        genderInputField.setText(GlobalData.genderUser)

        val genders = listOf(
            "Woman",
            "Man",
            "Transgender",
            "Non-binary",
            "Non-conforming",
            "Prefer not to respond"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genders)
        genderInputField.setAdapter(adapter)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            GlobalData.notificationUser = isChecked
        }

        btnSaveChanges.setOnClickListener {

            if (notificationSwitch.isChecked && !hasNotificationPermission()) {
                    requestPermission()
                    scheduleNotify()
                }

            val saltValue = BCrypt.gensalt()

            // Update user's information and notification settings based on UI input
            val updatedUser = User(
                user_id = GlobalData.userID!!,
                name = usernameInputEditText.text.toString(),
                passwordHash = BCrypt.hashpw(passwordInputEditText.text.toString(), saltValue),
                salt = saltValue,
                email = emailInputEditText.text.toString(),
                gender = genderInputField.text.toString(),
                notification = notificationSwitch.isChecked
            )
            // implement checks on the input data
            if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                lifecycleScope.launch {
                    dao.updateUser(updatedUser)
                }

                // Update global user data
                GlobalData.loggedInUser = updatedUser.name
                GlobalData.passwordUser = updatedUser.passwordHash
                GlobalData.emailUser = updatedUser.email
                GlobalData.genderUser = updatedUser.gender
                GlobalData.notificationUser = notificationSwitch.isChecked

                // Show success message
                Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
            } else {
                validateUsername()
                validateEmail()
                validatePassword()
                validateRepeatedPassword()
            }
        }

        // Link to change the security questions
        tvChangeSecurityQuestions.setOnClickListener {
            startActivity(Intent(this, SettingsSecurityQuestions::class.java))
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
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
                        .setNegativeButton("No"){_,_->
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
        val pendingIntent = getBroadcast(
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