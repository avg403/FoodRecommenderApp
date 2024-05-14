package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.adapter.RecipeSearchResultsAdapter

class RecipeLookupActivity : AppCompatActivity() {

    private lateinit var recipeSearchResultsAdapter: RecipeSearchResultsAdapter
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_lookup)

        val backButton = findViewById<AppCompatImageButton>(R.id.back_button2)
        backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val recipeNameSearchEditText = findViewById<EditText>(R.id.recipeNameSearchEditText)
        val recipeSearchResultsRecyclerView = findViewById<RecyclerView>(R.id.recipeSearchResultsRecyclerView)

        databaseHelper = DatabaseHelper(this)
        recipeSearchResultsAdapter = RecipeSearchResultsAdapter { recipe ->
            val intent = Intent(this, RecipeDetailsActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }
        recipeSearchResultsRecyclerView.adapter = recipeSearchResultsAdapter
        recipeSearchResultsRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeNameSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s.toString().trim()
                val searchResults = searchRecipesByName(searchQuery)
                recipeSearchResultsAdapter.setRecipes(searchResults)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchRecipesByName(searchQuery: String): List<Recipe> {
        val db = databaseHelper.readableDatabase
        val recipes = mutableListOf<Recipe>()

        val projection = arrayOf(
            DatabaseHelper.RECIPES_COLUMN_ID,
            DatabaseHelper.RECIPES_COLUMN_NAME,
            DatabaseHelper.RECIPES_COLUMN_INSTRUCTIONS,
            DatabaseHelper.RECIPES_COLUMN_IMAGE_FILE_PATH,
            DatabaseHelper.RECIPES_COLUMN_COOKING_TIME,
            DatabaseHelper.RECIPES_COLUMN_SERVINGS,
            DatabaseHelper.RECIPES_COLUMN_CUISINE
        )

        val selection = "${DatabaseHelper.RECIPES_COLUMN_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$searchQuery%")

        val cursor = db.query(
            DatabaseHelper.RECIPES_TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val recipeId = getInt(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_ID))
                val recipeName = getString(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_NAME))
                val instructions = getString(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_INSTRUCTIONS))
                val imageFilePath = getString(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_IMAGE_FILE_PATH))
                val cookingTime = getInt(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_COOKING_TIME))
                val servings = getInt(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_SERVINGS))
                val cuisine = getString(getColumnIndexOrThrow(DatabaseHelper.RECIPES_COLUMN_CUISINE))

                val recipe = Recipe(recipeId, recipeName, instructions, imageFilePath, cookingTime, servings, cuisine)
                recipes.add(recipe)
            }
        }

        cursor.close()
        return recipes
    }
}