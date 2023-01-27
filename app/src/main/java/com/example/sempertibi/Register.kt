package com.example.sempertibi

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    private val PASSWORD_PATTERN: Pattern =
        Pattern.compile(
            "^" +
                    //"(?=.*[0-9])" +           //at least 1 digit
                    //"(?=.*[a-z])" +           //at least 1 lower case letter
                    //"(?=.*[A-Z])" +           //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +          //any letter
                    "(?=.*[@#$%^&+=])" +        //at least 1 special character
                    "(?=\\S+$)" +               //no white spaces
                    ".{4,}" +                   //at least 4 characters
                    "$"
        )

    lateinit var relativeLayoutRegister: RelativeLayout
    lateinit var userInputField: TextInputLayout
    lateinit var passwordInputField: TextInputLayout
    lateinit var passwordRepeatInputField: TextInputLayout
    lateinit var emailInputField: TextInputLayout
    lateinit var genderInputField: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userInputField = findViewById(R.id.register_input_username)
        passwordInputField = findViewById(R.id.register_input_password)
        passwordRepeatInputField = findViewById(R.id.register_input_repeatPassword)
        emailInputField = findViewById(R.id.register_input_email)
        genderInputField = findViewById(R.id.register_input_setGender)

        val register = findViewById<Button>(R.id.btRegister)

        register.setOnClickListener {
            confirmInput()
            //var intent = Intent(this, SigninActivity::class.java)
            //startActivity(intent)
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

    // Validates the Username input in Registration process
    private fun validateUsername(): Boolean {
        val userInput = userInputField.editText?.text.toString().trim()
        if (userInput.isEmpty()) {
            userInputField.error = "Field can't be empty"
            return false
        } else if (userInput.length > 15) {
            userInputField.error = "Username too long"
            return false
        } else {
            userInputField.error = null
            return true
        }
    }

    // Validates the Password input in Registration process
    private fun validatePassword(): Boolean {
        val passwordInput = passwordInputField.editText?.text.toString().trim()
        if (passwordInput.isEmpty()) {
            passwordInputField.error = "Field can't be empty"
            return false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            passwordInputField.error = "Password too weak"
            return false
        } else {
            passwordInputField.error = null
            return true
        }
    }

    // Validates the repeated Password input in Registration process
    private fun validateRepeatedPassword(): Boolean {
        val passwordInput = passwordInputField.editText?.text.toString().trim()
        val repeatedPasswordInput = passwordRepeatInputField.editText?.text.toString().trim()
        if (repeatedPasswordInput.isEmpty()) {
            passwordRepeatInputField.error = "Field can't be empty"
            return false
        } else if (passwordInput != repeatedPasswordInput) {
            passwordRepeatInputField.error = "Passwords must be equal"
            return false
        } else {
            passwordRepeatInputField.error = null
            return true
        }
    }

    // Validates the Email input in Registration process
    private fun validateEmail(): Boolean {
        val emailInput = emailInputField.editText?.text.toString().trim()
        if (emailInput.isEmpty()) {
            emailInputField.error = "Field can't be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailInputField.error = "Please enter a valid email address"
            return false
        } else {
            emailInputField.error = null
            return true
        }
    }

    // Confirm all Input fields
    fun confirmInput() {
        if (!validateUsername() or !validatePassword() or !validateRepeatedPassword() or !validateEmail()) {
            return
        }
        var input = "Email: " + emailInputField.editText?.text.toString()
        input += "\n"
        input += "Username: " + userInputField.editText?.text.toString()
        input += "\n"
        input += "Password: " + passwordInputField.editText?.text.toString()
        input += "\n"
        input += "Password: " + passwordRepeatInputField.editText?.text.toString()
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }
}