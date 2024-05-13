package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.adapter.IngredientAdapter

class ContributeRecipeActivity : AppCompatActivity() {

    private lateinit var ingredientsAdapter: IngredientAdapter
    private val ingredients = mutableListOf<RecipeIngredient>()
    private val ingredientList = emptyList<Ingredient>()

    private lateinit var recipeNameEditText: EditText
    private lateinit var instructionsEditText: EditText
    private lateinit var imageFilePathEditText: EditText
    private lateinit var cookingTimeEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var cuisineEditText: EditText
    private lateinit var addIngredientButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var addRecipeButton: Button

    @SuppressLint("WrongViewCast")
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute_recipe)

        databaseHelper = DatabaseHelper(this)

        setupRecyclerView()
        setupButtons()
        setupEditTexts()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.ingredients_recycler_view)
        ingredientsAdapter = IngredientAdapter(ingredients, this, ingredientList, databaseHelper)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ingredientsAdapter
    }

    @SuppressLint("WrongViewCast")
    private fun setupButtons() {
        val backButton = findViewById<AppCompatImageButton>(R.id.back_button1)
        backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        addIngredientButton = findViewById(R.id.add_ingredient_button)
        addIngredientButton.setOnClickListener { addNewIngredient() }

        addRecipeButton = findViewById(R.id.add_recipe_button)
        addRecipeButton.setOnClickListener { addRecipe() }
    }

    private fun setupEditTexts() {
        recipeNameEditText = findViewById(R.id.recipe_name)
        instructionsEditText = findViewById(R.id.instructions)
        imageFilePathEditText = findViewById(R.id.image_file_path)
        cookingTimeEditText = findViewById(R.id.cooking_time)
        servingsEditText = findViewById(R.id.servings)
        cuisineEditText = findViewById(R.id.cuisine)

        // Set default values for the recipe details
        instructionsEditText.setText("")
        imageFilePathEditText.setText("")
        cookingTimeEditText.setText("0")
        servingsEditText.setText("1")
        cuisineEditText.setText("")

        ingredientsAdapter.addNewIngredient()
    }

    private fun addNewIngredient() {
        ingredientsAdapter.addNewIngredient()
    }

    private fun addRecipe() {
        val recipeName = recipeNameEditText.text.toString()
        val instructions = instructionsEditText.text.toString()
        val imageFilePath = imageFilePathEditText.text.toString()
        val cookingTime = cookingTimeEditText.text.toString().toIntOrNull() ?: 0
        val servings = servingsEditText.text.toString().toIntOrNull() ?: 0
        val cuisine = cuisineEditText.text.toString()

        if (recipeName.isNotEmpty() && instructions.isNotEmpty()) {
            val recipe = Recipe(recipeName, instructions, imageFilePath, cookingTime, servings, cuisine)
            val recipeId = databaseHelper.addRecipe(recipe, ingredients)

            if (recipeId != -1L) {
                showToastMessage("Recipe saved with ID: $recipeId")
                ingredientsAdapter.updateRecipeId(recipeId.toInt())
            } else {
                showToastMessage("Failed to save recipe")
            }
        } else {
            // Show an error message or handle the case where required fields are empty
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}