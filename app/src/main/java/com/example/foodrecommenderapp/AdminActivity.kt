package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils

class AdminActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        databaseHelper = DatabaseHelper(this)


        val backButton = findViewById<Button>(R.id.backButton2)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.manageRecipe).setOnClickListener {
            startActivity(Intent(this, ManageRecipesActivity::class.java))
        }

        findViewById<Button>(R.id.manageUser).setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
    }
}