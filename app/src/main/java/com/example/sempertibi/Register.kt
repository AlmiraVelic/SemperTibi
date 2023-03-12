package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class Register : AppCompatActivity() {
    private val passwordPattern: Pattern =
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

    lateinit var nestedScrollView: NestedScrollView
    lateinit var userInputFieldLayout: TextInputLayout
    lateinit var userInputFieldText: TextInputEditText
    lateinit var passwordInputFieldLayout: TextInputLayout
    lateinit var passwordInputFieldText: TextInputEditText
    lateinit var passwordRepeatInputFieldLayout: TextInputLayout
    lateinit var passwordRepeatInputFieldText: TextInputEditText
    lateinit var emailInputFieldLayout: TextInputLayout
    lateinit var emailInputFieldText: TextInputEditText
    lateinit var genderInputField: AutoCompleteTextView
    lateinit var btnRegister: Button
    lateinit var icon: ImageView

    private lateinit var appCompatTextViewLoginLink: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        // initializing the views
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
        icon = findViewById(R.id.logo)


        btnRegister.setOnClickListener {

            val insertUser = listOf(
                User(
                    user_id = 0,
                    name = userInputFieldText.text.toString(),
                    password = passwordInputFieldText.text.toString(),
                    gender = genderInputField.text.toString(),
                    email = emailInputFieldText.text.toString()
                )
            )

            if (validateUsername() and validatePassword() and validateRepeatedPassword() and validateEmail()) {
                lifecycleScope.launch {
                    insertUser.forEach { dao.addUser(it) }
                }
                emptyInputEditText()
                // Toast to show success message that record saved successfully
                Toast.makeText(
                    applicationContext,
                    getString(R.string.success_message),
                    Toast.LENGTH_SHORT
                ).show()
                btnRegister.visibility = View.INVISIBLE
            } else {
                validateUsername()
                validateEmail()
                validatePassword()
                validateRepeatedPassword()
            }
        }

        appCompatTextViewLoginLink.setOnClickListener{
            startActivity(Intent(this, SigninActivity::class.java))
        }

        icon.bringToFront()
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }

    // Validates the Username input in Registration process
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

    // Validates the Password input in Registration process
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

    // Validates the repeated Password input in Registration process
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

    // Validates the Email input in Registration process
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

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        userInputFieldText.text = null
        emailInputFieldText.text = null
        passwordInputFieldText.text = null
        passwordRepeatInputFieldText.text = null
        genderInputField.text = null
    }
}


/*
    // Confirm all Input fields
    private fun confirmInput() {
        if (!validateUsername() or !validatePassword() or !validateRepeatedPassword() or !validateEmail()) {
            return
        } else {
            emailInputFieldLayout.error = null
            passwordRepeatInputFieldLayout.error = null
            passwordInputFieldLayout.error = null
            userInputFieldLayout.error = null
        }
    }



    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {
        btnRegister.setOnClickListener(this)
        appCompatTextViewLoginLink.setOnClickListener(this)
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btRegister -> {
                confirmInput()
                //postDataToRoom(this)
            }
            //R.id.appCompatTextViewLoginLink -> finish()
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private fun postDataToRoom(view: Register) {
        //if (!databaseHelper.checkUser(emailInputFieldText.text.toString().trim())) {
        var insertUser = listOf(
            User(
                user_id = 0,
                name = userInputFieldText.text.toString().trim(),
                password = passwordInputFieldText.text.toString().trim(),
                gender = genderInputField.toString().trim(),
                email = emailInputFieldText.text.toString().trim()
            )
        )

        lifecycleScope.launch {
            insertUser.forEach { dao.addUser(it) }
        }

        // Snack Bar to show success message that record saved successfully
        Snackbar.make(
            nestedScrollView,
            getString(R.string.success_message),
            Snackbar.LENGTH_LONG
        ).show()
        emptyInputEditText()
        //startActivity(Intent(this, SigninActivity::class.java))
    }

        //} else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(
                nestedScrollView,
                getString(R.string.error_email_exists),
                Snackbar.LENGTH_LONG
            ).show()
        }
}
*/
