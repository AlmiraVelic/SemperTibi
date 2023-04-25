package com.example.sempertibi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.util.concurrent.Executor

@Suppress("IMPLICIT_CAST_TO_ANY")
class SigninActivity : AppCompatActivity() {

    private lateinit var userInputField: TextInputEditText
    private lateinit var passwordInputField: TextInputEditText
    private lateinit var relativeLayoutSignIn: RelativeLayout
    private lateinit var tvForgotPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var userInputFieldLayout: TextInputLayout
    private lateinit var passwordInputFieldLayout: TextInputLayout

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        StrictMode.enableDefaults()

        userInputField = findViewById(R.id.emailInput)
        passwordInputField = findViewById(R.id.passwordInput)
        tvForgotPassword = findViewById(R.id.tvForgotPW)
        relativeLayoutSignIn = findViewById(R.id.relativeLayout_signin)
        loginButton = findViewById(R.id.btLogin)
        registerButton = findViewById(R.id.btRegisterSignIn)
        userInputFieldLayout = findViewById(R.id.email)
        passwordInputFieldLayout = findViewById(R.id.password)

        val userDao = UserDatabase.getInstance(this).userDao()

        loginButton.setOnClickListener {

            val email = userInputField.text.toString().trim()
            val password = passwordInputField.text.toString().trim()

            lifecycleScope.launch {
                Log.d("CoroutineDebug", "Coroutine started")
                val userInDB = withContext(Dispatchers.IO) {
                    Log.d("CoroutineDebug", "Coroutine suspended: retrieving user")
                    userDao.getUserByMail(email)
                }
                Log.d("CoroutineDebug", "Coroutine resumed: user retrieved")

                if (validateInput()) {
                    //Verify the password against the stored hash using secure hashing algorithm BCrypt
                    if (userInDB != null && BCrypt.checkpw(password, userInDB.passwordHash)) {

                        GlobalData.userID = userInDB.user_id
                        GlobalData.loggedInUser = userInDB.name
                        GlobalData.passwordUser = password

                        executor = ContextCompat.getMainExecutor(this@SigninActivity)

                        biometricPrompt = androidx.biometric.BiometricPrompt(
                            this@SigninActivity,
                            executor,
                            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                                @SuppressLint("SetTextI18n")
                                override fun onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence
                                ) {
                                    super.onAuthenticationError(errorCode, errString)
                                    showMessage("Error, $errString")
                                    startActivity(Intent(this@SigninActivity,SecurityCheck::class.java))
                                }

                                @SuppressLint("SetTextI18n")
                                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    showMessage("Successful biometric authentication")

                                    // Authentication succeeded
                                    // set Global Data for Settings Activity

                                    GlobalData.emailUser = userInDB.email
                                    GlobalData.notificationUser = userInDB.notification
                                    GlobalData.genderUser = userInDB.gender

                                    showMessage("Login Successful")

                                    val intent =
                                        Intent(this@SigninActivity, MoodTracker::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                @SuppressLint("SetTextI18n")
                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    showMessage("Authentication Failed")
                                    startActivity(Intent(this@SigninActivity,SecurityCheck::class.java))
                                }
                            })

                        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric Authentication")
                            .setSubtitle("Login using fingerprint")
                            .setNegativeButtonText("Cancel")
                            .build()

                        biometricPrompt.authenticate(promptInfo)

                    } else {
                        Log.d("CoroutineDebug", "Authentication failed")
                        // Authentication failed
                        showMessage("Login failed, please check Email and Password")
                    }
                }
            }
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    // Shows a message in the bottom line of the Screen
    private fun showMessage(message: String) {
        Snackbar.make(relativeLayoutSignIn, message, Snackbar.LENGTH_SHORT).show()
    }

    // Validates the input of User and Password
    private fun validateInput(): Boolean {
        if (userInputField.text.toString().trim().isEmpty()) {
            showMessage("Please enter Username")
            userInputFieldLayout.error = "Please enter a valid email address"
            return false
        } else if (passwordInputField.text.toString().trim().isEmpty()) {
            showMessage("Please enter Password")
            passwordInputFieldLayout.error = "Field can't be empty"
            return false
        }
        return true
    }

}