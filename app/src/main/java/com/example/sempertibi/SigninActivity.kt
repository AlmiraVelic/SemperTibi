package com.example.sempertibi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.util.concurrent.Executor

class SigninActivity : AppCompatActivity() {

    private lateinit var userInputField: TextInputEditText
    private lateinit var passwordInputField: TextInputEditText
    private lateinit var relativeLayoutSignIn: RelativeLayout
    private lateinit var tvForgotPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        userInputField = findViewById(R.id.usernameInput)
        passwordInputField = findViewById(R.id.passwordInput)
        tvForgotPassword = findViewById(R.id.tvForgotPW)
        relativeLayoutSignIn = findViewById(R.id.relativeLayout_signin)
        loginButton = findViewById(R.id.btLogin)
        registerButton = findViewById(R.id.btRegisterSignIn)

        val userDao = UserDatabase.getInstance(this).userDao()
        loginButton.setOnClickListener {

            val user = userInputField.text.toString().trim()
            val password = passwordInputField.text.toString().trim()

            lifecycleScope.launch {
                Log.d("CoroutineDebug", "Coroutine started")
                val userInDB = withContext(Dispatchers.IO) {
                    Log.d("CoroutineDebug", "Coroutine suspended: retrieving user")
                    userDao.getUserByUsername(user)
                }
                Log.d("CoroutineDebug", "Coroutine resumed: user retrieved")

                if (validateInput()) {
                    //Verify the password against the stored hash using secure hashing algorithm BCrypt
                    if (userInDB != null && BCrypt.checkpw(password, userInDB.passwordHash)) {

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
                                }

                                @SuppressLint("SetTextI18n")
                                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    showMessage("Successful auth")

                                    // Authentication succeeded
                                    // set Global Data for Settings Activity
                                    GlobalData.userID = userInDB.user_id
                                    GlobalData.loggedInUser = user
                                    GlobalData.passwordUser = password
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
                                }
                            })

                        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric Authentication")
                            .setSubtitle("Login using fingerprint or face")
                            .setNegativeButtonText("Cancel")
                            .build()

                        biometricPrompt.authenticate(promptInfo)

                    } else {
                        Log.d("CoroutineDebug", "Authentication failed")
                        // Authentication failed
                        showMessage("Login failed")
                    }
                }
            }
        }

        tvForgotPassword.setOnClickListener{
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
            return false
        } else if (passwordInputField.text.toString().trim().isEmpty()) {
            showMessage("Please enter Password")
            return false
        }
        return true
    }

    private fun authenticateWithFingerprint(){

    }

}