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
import java.security.SecureRandom
import java.util.regex.Pattern
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class Register : AppCompatActivity() {
    private val passwordPattern: Pattern = Pattern.compile(
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
    lateinit var appCompatTextViewLoginLink: AppCompatTextView

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
            val saltValue = generateSalt()
            val insertUser = listOf(
                User(
                    user_id = 0,
                    name = userInputFieldText.text.toString(),
                    passwordHash = hashPassword(passwordInputFieldText.text.toString(), saltValue),
                    salt = saltValue,
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
                    applicationContext, getString(R.string.success_message), Toast.LENGTH_SHORT
                ).show()
                btnRegister.visibility = View.INVISIBLE
            } else {
                validateUsername()
                validateEmail()
                validatePassword()
                validateRepeatedPassword()
            }
        }

        appCompatTextViewLoginLink.setOnClickListener {
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

    /*
    The salt value should be unique for each user and should be generated using a secure random number generator.
    This helps to prevent attackers from using precomputed tables of hashes to attack multiple passwords at once.
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /*
    hash the password using a key derivation function called PBKDF2
     */
    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val iterations = 10000
        val keyLength = 256
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

}