package com.example.sempertibi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.sempertibi.data.UserDatabase
import com.example.sempertibi.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import java.util.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var resetInputLayout: TextInputLayout
    private lateinit var resetInputEmail:TextInputEditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // initializing the views
        resetInputLayout = findViewById(R.id.resetInputLayout)
        resetInputEmail = findViewById(R.id.emailInput)
        resetButton = findViewById(R.id.btReset)

        val email = resetInputEmail.text.toString().trim()
        val emailInput = resetInputLayout.editText?.text.toString().trim()

        // initializing the objects
        val dao = UserDatabase.getInstance(this).userDao()

        resetButton.setOnClickListener {
            if (validateEmail()) {
                lifecycleScope.launch {
                    val existingEntry = withContext(Dispatchers.IO) { dao.getUserByMail(email) }

                    if (existingEntry == null) {
                        // If there is no Entry, the user
                        AlertDialog.Builder(this@ForgotPassword).setTitle("E-Mail not found")
                            .setMessage("There is no entry for this E-Mail address. Please register your user or login with already existing username and password.")
                            .setPositiveButton("Create User") { _, _ ->
                                startActivity(Intent(this@ForgotPassword, Register::class.java))
                            }
                            .setNegativeButton("Login") { _, _ ->
                                startActivity(Intent(this@ForgotPassword,SigninActivity::class.java))
                            }
                            .show()
                    } else {
                        // If there is an entry for this email address, then user is notified
                        AlertDialog.Builder(this@ForgotPassword).setTitle("E-Mail found")
                            .setMessage("There is an entry for this E-Mail address. Do you wish to reset password?")
                            .setPositiveButton("Yes") { _, _ ->
                                val saltValue = BCrypt.gensalt()
                                // Update user's information and notification settings based on UI input
                                val updatedUser = User(
                                    user_id = existingEntry.user_id,
                                    name = existingEntry.name,
                                    passwordHash = BCrypt.hashpw(generateRandomPassword(), saltValue),
                                    salt = saltValue,
                                    email = existingEntry.email,
                                    gender = existingEntry.gender,
                                    notification = existingEntry.notification
                                )
                                lifecycleScope.launch {dao.updateUser(updatedUser)}

                                // Show success message
                                Toast.makeText(applicationContext, "Please check your E-Mails", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@ForgotPassword,SigninActivity::class.java))
                            }
                            .setNegativeButton("No"){_,_ ->
                                startActivity(Intent(this@ForgotPassword,SigninActivity::class.java))
                            }
                            .show()
                    }
                }
            }else{
                validateEmail()
            }
        }

    }

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
/*
    // TODO Send E-Mail to User -> Make an email address and check this
    private fun sendEmail(to: String, subject: String, message: String) {
        // Send email using your preferred email sending library
        // This is just an example using the JavaMail API
        val props = Properties()
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")

        val session = Session.getDefaultInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("your-email@gmail.com", "your-email-password")
                }
            })

        val messageToSend = MimeMessage(session)
        messageToSend.setFrom(InternetAddress("your-email@gmail.com"))
        messageToSend.setRecipient(Message.RecipientType.TO, InternetAddress(to))
        messageToSend.setSubject(subject)
        messageToSend.setText(message)

        Transport.send(messageToSend)
    }

 */

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