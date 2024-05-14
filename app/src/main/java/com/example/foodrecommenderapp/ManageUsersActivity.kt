package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class ManageUsersActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        val backButton = findViewById<ImageButton>(R.id.back_button9)
        backButton.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }

        databaseHelper = DatabaseHelper(this)

        displayUsers()
    }

    private fun displayUsers() {
        val users = databaseHelper.getAllUsers()
        val userListView = findViewById<LinearLayout>(R.id.userListContainer)

        userListView.removeAllViews() // Clear any existing views

        users.forEach { user ->
            val userItemView = layoutInflater.inflate(R.layout.user_item, null)
            userItemView.findViewById<TextView>(R.id.usernameTextView).text = user.username
            userItemView.findViewById<ImageButton>(R.id.deleteUserButton).setOnClickListener {
                databaseHelper.deleteUser(user.userId)
                displayUsers() // Refresh the list after deleting
            }
            userListView.addView(userItemView)
        }
    }
}