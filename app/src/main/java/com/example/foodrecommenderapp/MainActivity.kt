package com.example.foodrecommenderapp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        // Set up the login button click listener
        findViewById<Button>(R.id.frontPageLoginButton).setOnClickListener {
            val username = findViewById<EditText>(R.id.usernameInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()

            if (username.isBlank() || password.isBlank()) {
                showErrorMessage("Username and password cannot be blank")
                return@setOnClickListener
            }

            val hashedPassword = hashPassword(password)
            val authenticated = databaseHelper.authenticateUser(username, hashedPassword)

            if (authenticated) {
                // Redirect to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else if (username == "admin" && password == "admin") {
                // Redirect to AdminActivity
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            } else {
                showErrorMessage("Invalid username or password")
            }
        }

        // Set up the sign-up button click listener
        findViewById<TextView>(R.id.frontPageSignUpButton).setOnClickListener {
            showSignUpDialog()
        }
    }

    private fun showSignUpDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.signup_dialog, null)
        builder.setView(dialogView)

        val usernameEditText = dialogView.findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
        val signUpButton = dialogView.findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate username and password
            if (username.isBlank() || password.isBlank()) {
                showErrorMessage("Username and password cannot be blank")
                return@setOnClickListener
            }

            // Check if the username already exists
            if (databaseHelper.usernameExists(username)) {
                showErrorMessage("Username already exists")
                return@setOnClickListener
            }

            // Hash the password
            val hashedPassword = hashPassword(password)

            // Add the new user to the database
            val userId = databaseHelper.addUser(username, hashedPassword)

            if (userId.toInt() != -1) {
                // Sign-up successful
                showSuccessMessage("Sign-up successful")
            } else {
                // Sign-up failed
                showErrorMessage("Sign-up failed")
            }
        }

        builder.setPositiveButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun hashPassword(password: String): String {
        // Implement password hashing using a secure algorithm like SHA-256 or bcrypt
        // For example, using SHA-256:
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun showErrorMessage(message: String) {
        // Show an error message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccessMessage(message: String) {
        // Show a success message to the user
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}