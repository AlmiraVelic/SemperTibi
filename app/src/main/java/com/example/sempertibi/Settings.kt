package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class Settings : AppCompatActivity() {

    lateinit var notificationSwitch: Switch
    lateinit var usernameInputEditText: TextInputEditText
    lateinit var passwordInputEditText: TextInputEditText
    lateinit var passwordRepeatEditText: TextInputEditText
    lateinit var emailInputEditText: TextInputEditText
    lateinit var genderInputField: AutoCompleteTextView
    lateinit var btnSaveChanges: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val dao = UserDatabase.getInstance(this).userDao()

        notificationSwitch = findViewById(R.id.switch1)
        usernameInputEditText = findViewById(R.id.usernameInput)
        passwordInputEditText = findViewById(R.id.passwordInput)
        passwordRepeatEditText = findViewById(R.id.repeatPasswordInput)
        emailInputEditText = findViewById(R.id.emailInput)
        genderInputField = findViewById(R.id.input_gender)
        btnSaveChanges = findViewById(R.id.btSave)

        /*
        TODO Implement user input checks as in registration
         */

        notificationSwitch.isChecked = GlobalData.notificationUser ?: false
        usernameInputEditText.setText(GlobalData.loggedInUser)
        passwordInputEditText.setText(GlobalData.passwordUser)
        passwordRepeatEditText.setText(GlobalData.passwordUser)
        emailInputEditText.setText(GlobalData.emailUser)
        genderInputField.setText(GlobalData.genderUser)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            GlobalData.notificationUser = isChecked
        }

        btnSaveChanges.setOnClickListener {
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
        }

        val icon = findViewById<ImageView>(R.id.logo)
        icon.bringToFront()

        val scrollView = findViewById<NestedScrollView>(R.id.nestedScrollView)
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }
}