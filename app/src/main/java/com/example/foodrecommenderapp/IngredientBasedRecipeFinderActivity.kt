package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.adapter.RecommendedRecipesAdapter

class IngredientBasedRecipeFinderActivity : AppCompatActivity() {

    private lateinit var recommendedRecipesAdapter: RecommendedRecipesAdapter

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient_based_recipe_finder)

        // Initialize RecyclerView
        val recommendedRecipesRecyclerView = findViewById<RecyclerView>(R.id.recommendedRecipesRecyclerView)
        recommendedRecipesAdapter = RecommendedRecipesAdapter { recipe ->
            // Launch the RecipeDetailsActivity with the clicked recipe
            val intent = Intent(this, RecipeDetailsActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }
        recommendedRecipesRecyclerView.adapter = recommendedRecipesAdapter
        recommendedRecipesRecyclerView.layoutManager = LinearLayoutManager(this)

        val backButton = findViewById<AppCompatImageButton>(R.id.back_button3)
        backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val ingredientInput = findViewById<AppCompatEditText>(R.id.ingredientInput)
        val recommendButton = findViewById<AppCompatButton>(R.id.recommendButton)

        recommendButton.setOnClickListener {
            val userIngredients = ingredientInput.text.toString().split(",").map { it.trim() }
            val recommendedRecipes = findRecommendedRecipes(userIngredients)
            displayRecommendedRecipes(recommendedRecipes)
        }
    }

    private fun findRecommendedRecipes(userIngredients: List<String>): List<Recipe> {
        val databaseHelper = DatabaseHelper(this)
        val recommendedRecipes = mutableListOf<Recipe>()

        // Get all recipes from the database
        val allRecipes = getRecipesFromDatabase(databaseHelper)

        // Filter recipes based on user ingredients
        for (recipe in allRecipes) {
            val recipeIngredients = getRecipeIngredients(databaseHelper, recipe.recipeId)
            val matchingIngredients = recipeIngredients.filter { userIngredients.map { it.lowercase() }.contains(it.ingredientName.lowercase()) }

            // Rank recipes based on the number of matching ingredients
            if (matchingIngredients.isNotEmpty()) {
                recommendedRecipes.add(recipe)
            }
        }

        // Sort recommended recipes based on the number of matching ingredients (descending order)
        recommendedRecipes.sortByDescending { recipe ->
            val recipeIngredients = getRecipeIngredients(databaseHelper, recipe.recipeId)
            val matchingIngredients = recipeIngredients.filter { userIngredients.map { it.lowercase() }.contains(it.ingredientName.lowercase()) }
            matchingIngredients.size
        }

        return recommendedRecipes
    }




    private fun getRecipesFromDatabase(databaseHelper: DatabaseHelper): List<Recipe> {
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

        val cursor = db.query(
            DatabaseHelper.RECIPES_TABLE_NAME,
            projection,
            null,
            null,
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

    private fun getRecipeIngredients(databaseHelper: DatabaseHelper, recipeId: Int): List<RecipeIngredient> {
        val db = databaseHelper.readableDatabase
        val recipeIngredients = mutableListOf<RecipeIngredient>()

        val projection = arrayOf(
            DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID,
            DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_QUANTITY,
            DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_UNIT,
            DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT
        )

        val selection = "${DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_RECIPE_ID} = ?"
        val selectionArgs = arrayOf(recipeId.toString())

        val cursor = db.query(
            DatabaseHelper.RECIPE_INGREDIENTS_TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val ingredientId = getInt(getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID))
                val quantity = getFloat(getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_QUANTITY))
                val unit = getString(getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_UNIT))
                val isMainIngredient = getInt(getColumnIndexOrThrow(DatabaseHelper.RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT))

                val ingredientName = databaseHelper.getIngredientName(ingredientId)

                val recipeIngredient = RecipeIngredient(recipeId, ingredientId, quantity, unit, ingredientName, isMainIngredient)
                recipeIngredients.add(recipeIngredient)
            }
        }

        cursor.close()
        return recipeIngredients
    }


    private fun displayRecommendedRecipes(recommendedRecipes: List<Recipe>) {
        recommendedRecipesAdapter.setRecipes(recommendedRecipes)
    }


}