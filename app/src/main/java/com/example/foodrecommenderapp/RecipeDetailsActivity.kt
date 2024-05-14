package com.example.foodrecommenderapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

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
        val recipeImageView = findViewById<ImageView>(R.id.recipeImageView)
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


            if (it.imageFilePath.isNotEmpty()) {
                val imageFile = File(it.imageFilePath)
                if (imageFile.exists()) {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(imageFile.absolutePath, options)
                    val imageWidth = options.outWidth
                    val imageHeight = options.outHeight
                    val scaleFactor = calculateScaleFactor(imageWidth, imageHeight, resources.displayMetrics.widthPixels)
                    options.inJustDecodeBounds = false
                    options.inSampleSize = scaleFactor
                    try {
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                        recipeImageView.setImageBitmap(bitmap)
                    } catch (e: OutOfMemoryError) {
                        Log.e("MyApp", "Out of memory error while loading image", e)
                    }
                } else {
                    // Handle the case when no image is available
                    recipeImageView.setImageResource(R.drawable.luffy_eating) // Replace with your default image resource
                }
            } else {
                // Handle the case when no image is available
                recipeImageView.setImageResource(R.drawable.luffy_eating) // Replace with your default image resource
            }
        }
    }
    private fun calculateScaleFactor(imageWidth: Int, imageHeight: Int, maxWidth: Int): Int {
        val targetWidth = maxWidth
        val targetHeight = maxWidth * imageHeight / imageWidth
        val scaleFactor = Math.min(
            imageWidth / targetWidth,
            imageHeight / targetHeight
        )
        return Math.max(1, Integer.highestOneBit(scaleFactor))
    }
}