package com.example.sempertibi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.util.regex.Pattern

class Settings : AppCompatActivity() {

    private val passwordPattern: Pattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +           //at least 1 digit
                //"(?=.*[a-z])" +           //at least 1 lower case letter
                //"(?=.*[A-Z])" +           //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +          //any letter
                "(?=.*[@#$%^&+=])" +        //at least 1 special character
                "(?=\\S+$)" +               //no white spaces
                ".{4,}" +                   //at least 4 characters
                "$"
    )

    lateinit var notificationSwitch: Switch

    lateinit var userInputFieldLayout: TextInputLayout
    lateinit var passwordInputFieldLayout: TextInputLayout
    lateinit var passwordRepeatInputFieldLayout: TextInputLayout
    lateinit var emailInputFieldLayout: TextInputLayout

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

        userInputFieldLayout = findViewById(R.id.username)
        passwordInputFieldLayout = findViewById(R.id.password)
        passwordRepeatInputFieldLayout = findViewById(R.id.repeatPassword)
        emailInputFieldLayout = findViewById(R.id.email)
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

}