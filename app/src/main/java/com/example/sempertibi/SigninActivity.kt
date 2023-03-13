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

    lateinit var userInputField: TextInputEditText
    lateinit var passwordInputField: TextInputEditText
    lateinit var relativeLayoutSignIn: RelativeLayout
    lateinit var loginbtn: Button
    lateinit var registerbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        userInputField = findViewById(R.id.usernameInput)
        passwordInputField = findViewById(R.id.passwordInput)
        relativeLayoutSignIn = findViewById(R.id.relativeLayout_signin)
        loginbtn = findViewById(R.id.btLogin)
        registerbtn = findViewById(R.id.btRegisterSignIn)

        val userDao = UserDatabase.getInstance(this).userDao()
        loginbtn.setOnClickListener {

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
                        showMessage("Login Successful")
                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@SigninActivity, Dashboard::class.java)
                            intent.putExtra("USER", user)
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

        registerbtn.setOnClickListener {
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