package com.example.foodrecommenderapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val backButton = findViewById<ImageButton>(R.id.back_button5)
        backButton.setOnClickListener {
            onBackPressed()
        }


        val recipeNameTextView = findViewById<TextView>(R.id.recipeNameTextView)
        val ingredientsTextView = findViewById<TextView>(R.id.ingredientTextView)
        val instructionsTextView = findViewById<TextView>(R.id.instructionsTextView)
        val cuisineTextView = findViewById<TextView>(R.id.cuisineTextView)
        val servingsTextView = findViewById<TextView>(R.id.servingsTextView)
        val cookingTimeTextView = findViewById<TextView>(R.id.cookingTimeTextView)

        val recipe = intent.getSerializableExtra("recipe") as? Recipe

        recipe?.let {
            recipeNameTextView.text = it.recipeName
            instructionsTextView.text = it.instructions
            cuisineTextView.text = "Cuisine: ${it.cuisine}"
            servingsTextView.text = "Servings: ${it.servings}"
            cookingTimeTextView.text = "Cooking Time: ${it.cookingTime} minutes"

            val databaseHelper = DatabaseHelper(this)
            val recipeIngredients = databaseHelper.getRecipeIngredients(it.recipeId)
            val ingredientsList = recipeIngredients.joinToString("\n") { "${it.ingredientName} (${it.quantity} ${it.unit})" }
            ingredientsTextView.text = ingredientsList
        }
    }
}