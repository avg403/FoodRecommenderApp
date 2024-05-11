package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
    }

    private fun addNewIngredient() {
        ingredientsAdapter.addNewIngredient()
    }

    private fun addRecipe() {
        // Add recipe logic here
    }
}