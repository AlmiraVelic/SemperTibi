package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


class Register : AppCompatActivity(), View.OnClickListener {
    private val activity = this@Register

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

    lateinit var nestedScrollView: NestedScrollView
    lateinit var userInputFieldLayout: TextInputLayout
    lateinit var userInputFieldText: TextInputEditText
    lateinit var passwordInputFieldLayout: TextInputLayout
    lateinit var passwordInputFieldText: TextInputEditText
    lateinit var passwordRepeatInputFieldLayout: TextInputLayout
    lateinit var passwordRepeatInputFieldText: TextInputEditText
    lateinit var emailInputFieldLayout: TextInputLayout
    lateinit var emailInputFieldText: TextInputEditText
    lateinit var genderInputField: TextInputLayout
    lateinit var btnRegister: Button
    lateinit var icon: ImageView

    private lateinit var appCompatTextViewLoginLink: AppCompatTextView

    private lateinit var databaseHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()

        icon.bringToFront()
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                icon.visibility = View.INVISIBLE
            } else {
                icon.visibility = View.VISIBLE
            }
        }
    }


    private fun initViews() {
        userInputFieldLayout = findViewById(R.id.register_input_username)
        userInputFieldText = findViewById(R.id.usernameInput)
        passwordInputFieldLayout = findViewById(R.id.register_input_password)
        passwordInputFieldText = findViewById(R.id.passwordInput)
        passwordRepeatInputFieldLayout = findViewById(R.id.register_input_repeatPassword)
        passwordRepeatInputFieldText = findViewById(R.id.repeatPasswordInput)
        emailInputFieldLayout = findViewById(R.id.register_input_email)
        emailInputFieldText = findViewById(R.id.emailInput)
        genderInputField = findViewById(R.id.register_input_setGender)
        btnRegister = findViewById(R.id.btRegister)
        appCompatTextViewLoginLink = findViewById(R.id.appCompatTextViewLoginLink)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        icon = findViewById(R.id.logo)
    }


    // Validates the Username input in Registration process
    private fun validateUsername(): Boolean {
        val userInput = userInputFieldLayout.editText?.text.toString().trim()
        if (userInput.isEmpty()) {
            userInputFieldLayout.error = "Field can't be empty"
            return false
        } else if (userInput.length > 15) {
            userInputFieldLayout.error = "Username too long"
            return false
        } else {
            userInputFieldLayout.error = null
            return true
        }
    }

    // Validates the Password input in Registration process
    private fun validatePassword(): Boolean {
        val passwordInput = passwordInputFieldLayout.editText?.text.toString().trim()
        if (passwordInput.isEmpty()) {
            passwordInputFieldLayout.error = "Field can't be empty"
            return false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordInputFieldLayout.error = "Password too weak - min. 4 characters! Use upper and lowercase letters, numbers, and special symbols like @#\$%^&+="
            return false
        } else {
            passwordInputFieldLayout.error = null
            return true
        }
    }

    // Validates the repeated Password input in Registration process
    private fun validateRepeatedPassword(): Boolean {
        val passwordInput = passwordInputFieldLayout.editText?.text.toString().trim()
        val repeatedPasswordInput = passwordRepeatInputFieldLayout.editText?.text.toString().trim()
        if (repeatedPasswordInput.isEmpty()) {
            passwordRepeatInputFieldLayout.error = "Field can't be empty"
            return false
        } else if (passwordInput != repeatedPasswordInput) {
            passwordRepeatInputFieldLayout.error = "Passwords must be equal"
            return false
        } else {
            passwordRepeatInputFieldLayout.error = null
            return true
        }
    }

    // Validates the Email input in Registration process
    private fun validateEmail(): Boolean {
        val emailInput = emailInputFieldLayout.editText?.text.toString().trim()
        if (emailInput.isEmpty()) {
            emailInputFieldLayout.error = "Field can't be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailInputFieldLayout.error = "Please enter a valid email address"
            return false
        } else {
            emailInputFieldLayout.error = null
            return true
        }
    }

    // Confirm all Input fields
    fun confirmInput() {
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
     * This method is to initialize objects to be used
     */
    private fun initObjects() {
        databaseHelper = UserDbHelper(activity)
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
                //postDataToSQLite()
                //startActivity(Intent(this, SigninActivity::class.java))
            }
            R.id.appCompatTextViewLoginLink -> finish()
        }
    }

    /**
     * This method is to validate the input text fields and post data to SQLite
     */
    private fun postDataToSQLite() {

        //if (!databaseHelper.checkUser(emailInputFieldText.text.toString().trim())) {
            var user = UserModel(
                id = 1,
                name = userInputFieldText.text.toString().trim(),
                password = passwordInputFieldText.text.toString().trim(),
                email = emailInputFieldText.text.toString().trim(),
                gender = genderInputField.toString().trim()
            )
            databaseHelper.addUser(user)
            // Snack Bar to show success message that record saved successfully
            Snackbar.make(
                nestedScrollView,
                getString(R.string.success_message),
                Snackbar.LENGTH_LONG
            ).show()
            emptyInputEditText()

/*
        //} else {
            // Snack Bar to show error message that record already exists
            Snackbar.make(
                nestedScrollView,
                getString(R.string.error_email_exists),
                Snackbar.LENGTH_LONG
            ).show()
        }
*/
    }

    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        userInputFieldText.text = null
        emailInputFieldText.text = null
        passwordInputFieldText.text = null
        passwordRepeatInputFieldText.text = null

    }
}