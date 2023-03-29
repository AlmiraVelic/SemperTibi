package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class SigninActivity : AppCompatActivity() {

    private lateinit var userInputField: TextInputEditText
    private lateinit var passwordInputField: TextInputEditText
    private lateinit var relativeLayoutSignIn: RelativeLayout
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        userInputField = findViewById(R.id.usernameInput)
        passwordInputField = findViewById(R.id.passwordInput)
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
                    if (userInDB != null && BCrypt.checkpw(password,userInDB.passwordHash)) {
                        Log.d("CoroutineDebug", "Authentication succeeded")
                        // Authentication succeeded

                        // set Global Data for Settings Activity
                        GlobalData.userID = userInDB.user_id
                        GlobalData.loggedInUser = user
                        GlobalData.passwordUser = password
                        GlobalData.emailUser = userInDB.email
                        GlobalData.notificationUser = userInDB.notification
                        GlobalData.genderUser = userInDB.gender

                        showMessage("Login Successful")
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@SigninActivity, MoodTracker::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.d("CoroutineDebug", "Authentication failed")
                        // Authentication failed
                        showMessage("Login failed")
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
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
}