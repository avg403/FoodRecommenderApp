package com.example.foodrecommenderapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val contributeRecipeButton = findViewById<Button>(R.id.contributeRecipeButton)
        contributeRecipeButton.setOnClickListener {
            startActivity(Intent(this, ContributeRecipeActivity::class.java))
            finish()
        }

        val recipeLookupButton = findViewById<Button>(R.id.recipeLookupButton)
        recipeLookupButton.setOnClickListener {
            startActivity(Intent(this, RecipeLookupActivity::class.java))
            finish()
        }

        val ingredientBasedRecipeFinderButton = findViewById<Button>(R.id.ingredientBasedRecipeFinderButton)
        ingredientBasedRecipeFinderButton.setOnClickListener {
            startActivity(Intent(this, IngredientBasedRecipeFinderActivity::class.java))
            finish()
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}