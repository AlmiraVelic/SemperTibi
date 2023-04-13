package com.example.sempertibi

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import java.security.SecureRandom

class ForgotPassword : AppCompatActivity() {

    private lateinit var resetInputLayout: TextInputLayout
    private lateinit var resetInputEmail: TextInputEditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        StrictMode.enableDefaults()

        // initializing the views
        resetInputLayout = findViewById(R.id.resetInputLayout)
        resetInputEmail = findViewById(R.id.emailInput)
        resetButton = findViewById(R.id.btReset)

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        resetButton.setOnClickListener {
            if (validateEmail()) {
                lifecycleScope.launch {
                    val existingEntry =
                        withContext(Dispatchers.IO) { dao.getUserByMail(resetInputEmail.text.toString()) }

                    if (existingEntry == null) {
                        // If there is no Entry, the user
                        AlertDialog.Builder(this@ForgotPassword).setTitle("E-Mail not found")
                            .setMessage("There is no entry for this E-Mail address. Please register your user or login with already existing username and password.")
                            .setPositiveButton("Create User") { _, _ ->
                                startActivity(Intent(this@ForgotPassword, Register::class.java))
                            }
                            .setNegativeButton("Login") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@ForgotPassword,
                                        SigninActivity::class.java
                                    )
                                )
                            }
                            .show()
                    } else {
                        // If there is an entry for this email address, then user is notified
                        AlertDialog.Builder(this@ForgotPassword).setTitle("E-Mail found")
                            .setMessage("There is an entry for this E-Mail address. Do you wish to reset password?")
                            .setPositiveButton("Yes") { _, _ ->
                                val saltValue = BCrypt.gensalt()
                                val newPassword = generateRandomPassword()
                                // Update user's information and notification settings based on UI input
                                val updatedUser = User(
                                    user_id = existingEntry.user_id,
                                    name = existingEntry.name,
                                    passwordHash = BCrypt.hashpw(
                                        newPassword,
                                        saltValue
                                    ),
                                    salt = saltValue,
                                    email = existingEntry.email,
                                    gender = existingEntry.gender,
                                    notification = existingEntry.notification
                                )
                                lifecycleScope.launch { dao.updateUser(updatedUser) }

                                val user = existingEntry.name
                                val mailMessage =
                                    "<h1>Reset Password</h1>" +
                                            "<p>Dear $user,<br><br>Your new password is $newPassword <br><br>" +
                                            "Enjoy the usage of <b>SemperTibi!</b></p><br><br>"+
                                            "<p>Please change this password in your settings after login. Thank you!</p>"
                                sendEmail(resetInputEmail.text.toString(), user, mailMessage)

                                // Show success message
                                Toast.makeText(
                                    applicationContext,
                                    "Please check your E-Mails",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(
                                    Intent(
                                        this@ForgotPassword,
                                        SigninActivity::class.java
                                    )
                                )
                            }
                            .setNegativeButton("No") { _, _ ->
                                startActivity(
                                    Intent(
                                        this@ForgotPassword,
                                        SigninActivity::class.java
                                    )
                                )
                            }
                            .show()
                    }
                }
            } else {
                validateEmail()
            }
        }

    }

    /**
     * Generates a random password that is secure
     */
    private fun generateRandomPassword(): String {
        // Generate a random password of length 5 with letters, special chars and numbers
        val passwordLength = 5
        val specialChars = "@#$%^&+="
        val random = SecureRandom()

        val password = StringBuilder()
        while (password.length < passwordLength) {
            when (random.nextInt(4)) {
                0 -> password.append(random.nextInt(10)) // add digit
                1 -> password.append(('a'..'z').random()) // add lowercase letter
                2 -> password.append(('A'..'Z').random()) // add uppercase letter
                3 -> password.append(specialChars.random()) // add special character
            }
        }
        return password.toString()
    }

    /**
     * Sends Mails to Users saved mail address
     */
    private fun sendEmail(email: String, name: String, message: String) {
        val sendgridAPIKey = BuildConfig.API_KEY
        val sendGrid = SendGrid.create(sendgridAPIKey)

        val mail = SendGridMail()
        mail.addRecipient(email, name)
        mail.setFrom("sempertibi.app@gmail.com", "SemperTibi App")
        mail.setSubject("Password Reset for SemperTibi")
        mail.setHtmlContent(message)

        val task = SendTask(sendGrid)
        task.send(mail)
    }


    /*
    Validates the Email input in Registration process
     */
    private fun validateEmail(): Boolean {
        val emailInput = resetInputLayout.editText?.text.toString().trim()
        return if (emailInput.isEmpty()) {
            resetInputLayout.error = "Field can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            resetInputLayout.error = "Please enter a valid email address"
            false
        } else {
            resetInputLayout.error = null
            true
        }
    }
}