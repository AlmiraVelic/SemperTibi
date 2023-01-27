package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class SigninActivity : AppCompatActivity() {

    lateinit var userInputField : TextInputEditText
    lateinit var passwordInputField : TextInputEditText
    lateinit var relativeLayoutSignIn : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        userInputField = findViewById(R.id.usernameInput)
        passwordInputField = findViewById(R.id.passwordInput)
        relativeLayoutSignIn = findViewById(R.id.relativeLayout_signin)

        var user = ""
        val login = findViewById<Button>(R.id.btLogin)

        login.setOnClickListener{
            user = userInputField.text.toString()
            if (validateInput()){
                showMessage("Login Successful")
                var intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("USER", user)
                startActivity(intent)
            }
        }

        val register = findViewById<Button>(R.id.btRegister)
        register.setOnClickListener{
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
    // Shows a message in the bottom line of the Screen
    private fun showMessage(message: String){
        Snackbar.make(relativeLayoutSignIn, message, Snackbar.LENGTH_SHORT).show()
    }

    // Validates the input of User and Password
    private fun validateInput():Boolean{
        if (userInputField.text.toString().trim().isEmpty()){
            showMessage("Please enter Username")
            return false
        } else if (passwordInputField.text.toString().trim().isEmpty()){
            showMessage("Please enter Password")
            return false
        }
        return true
    }
}