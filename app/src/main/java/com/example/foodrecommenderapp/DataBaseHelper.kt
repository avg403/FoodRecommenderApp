package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "FoodRecommenderDB.db", null, 1) {

    companion object {
        const val RECIPES_TABLE_NAME = "Recipes"
        const val RECIPES_COLUMN_ID = "recipeId"
        const val RECIPES_COLUMN_NAME = "recipeName"
        const val RECIPES_COLUMN_INSTRUCTIONS = "instructions"
        const val RECIPES_COLUMN_IMAGE_FILE_PATH = "imageFilePath"
        const val RECIPES_COLUMN_COOKING_TIME = "cookingTime"
        const val RECIPES_COLUMN_SERVINGS = "servings"
        const val RECIPES_COLUMN_CUISINE = "cuisine"

        const val INGREDIENTS_TABLE_NAME = "Ingredients"
        const val INGREDIENTS_COLUMN_ID = "ingredientId"
        const val INGREDIENTS_COLUMN_NAME = "ingredientName"

        const val RECIPE_INGREDIENTS_TABLE_NAME = "RecipeIngredients"
        const val RECIPE_INGREDIENTS_COLUMN_RECIPE_ID = "recipeId"
        const val RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID = "ingredientId"
        const val RECIPE_INGREDIENTS_COLUMN_QUANTITY = "quantity"
        const val RECIPE_INGREDIENTS_COLUMN_UNIT = "unit"
        const val RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT = "isMainIngredient"
    }

    // this is called the first time a database is accessed. There should be code in here to create a new db
    override fun onCreate(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            val createRecipesTableStatement = "CREATE TABLE $RECIPES_TABLE_NAME(" +
                    "$RECIPES_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$RECIPES_COLUMN_NAME TEXT NOT NULL," +
                    "$RECIPES_COLUMN_INSTRUCTIONS TEXT," +
                    "$RECIPES_COLUMN_IMAGE_FILE_PATH TEXT," +
                    "$RECIPES_COLUMN_COOKING_TIME INTEGER," +
                    "$RECIPES_COLUMN_SERVINGS INTEGER," +
                    "$RECIPES_COLUMN_CUISINE TEXT" +
                    ");"
            db.execSQL(createRecipesTableStatement)

            val createIngredientsTableStatement = "CREATE TABLE $INGREDIENTS_TABLE_NAME(" +
                    "$INGREDIENTS_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$INGREDIENTS_COLUMN_NAME TEXT NOT NULL UNIQUE" +
                    ");"
            db.execSQL(createIngredientsTableStatement)

            val createRecipeIngredientsTableStatement =
                "CREATE TABLE $RECIPE_INGREDIENTS_TABLE_NAME(" +
                        "$RECIPE_INGREDIENTS_COLUMN_RECIPE_ID INTEGER NOT NULL," +
                        "$RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID INTEGER NOT NULL," +
                        "$RECIPE_INGREDIENTS_COLUMN_QUANTITY REAL," +
                        "$RECIPE_INGREDIENTS_COLUMN_UNIT TEXT," +
                        "$RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT INTEGER NOT NULL DEFAULT 0," +
                        "FOREIGN KEY($RECIPE_INGREDIENTS_COLUMN_RECIPE_ID) REFERENCES $RECIPES_TABLE_NAME($RECIPES_COLUMN_ID)," +
                        "FOREIGN KEY($RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID) REFERENCES $INGREDIENTS_TABLE_NAME($INGREDIENTS_COLUMN_ID)" +
                        ");"
            db.execSQL(createRecipeIngredientsTableStatement)

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Placeholder for upgrading tables
    }


    @SuppressLint("Range")
    fun getIngredients(): List<Map<String, String>> {
        val selectQuery =
            "SELECT $INGREDIENTS_COLUMN_ID, $INGREDIENTS_COLUMN_NAME FROM $INGREDIENTS_TABLE_NAME"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        val ingredients = mutableListOf<Map<String, String>>()
        if (cursor.moveToFirst()) {
            do {
                val ingredient = HashMap<String, String>()
                ingredient["ingredientId"] =
                    cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN_ID))
                ingredient["ingredientName"] =
                    cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN_NAME))
                ingredients.add(ingredient)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredients
    }


    fun addRecipe(recipe: Recipe, ingredients: List<RecipeIngredient>): Long {
        val db = writableDatabase
        db.beginTransaction()

        var recipeId: Long = -1

        try {
            // Insert the recipe into the Recipes table
            val recipeValues = ContentValues().apply {
                put(RECIPES_COLUMN_NAME, recipe.recipeName)
                put(RECIPES_COLUMN_INSTRUCTIONS, recipe.instructions)
                put(RECIPES_COLUMN_IMAGE_FILE_PATH, recipe.imageFilePath)
                put(RECIPES_COLUMN_COOKING_TIME, recipe.cookingTime)
                put(RECIPES_COLUMN_SERVINGS, recipe.servings)
                put(RECIPES_COLUMN_CUISINE, recipe.cuisine)
            }
            recipeId = db.insert(RECIPES_TABLE_NAME, null, recipeValues)

            // Insert the ingredients into the RecipeIngredients table
            if (recipeId != -1L) {
                for (recipeIngredient in ingredients) {
                    val ingredientValues = ContentValues().apply {
                        put(RECIPE_INGREDIENTS_COLUMN_RECIPE_ID, recipeId)
                        put(RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID, recipeIngredient.ingredientId)
                        put(RECIPE_INGREDIENTS_COLUMN_QUANTITY, recipeIngredient.quantity)
                        put(RECIPE_INGREDIENTS_COLUMN_UNIT, recipeIngredient.unit)
                        put(
                            RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT,
                            recipeIngredient.isMainIngredient
                        )
                    }
                    db.insert(RECIPE_INGREDIENTS_TABLE_NAME, null, ingredientValues)
                }
                db.setTransactionSuccessful()
            } else {
                // Failed to insert recipe
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }

        return recipeId
    }

    private fun ingredientExists(ingredientName: String): Boolean {
        val query = "SELECT * FROM $INGREDIENTS_TABLE_NAME WHERE $INGREDIENTS_COLUMN_NAME = ?"
        val selectionArgs = arrayOf(ingredientName)
        val cursor = readableDatabase.rawQuery(query, selectionArgs)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }


    @SuppressLint("Range")
    fun getIngredientName(ingredientId: Int): String {
        val query =
            "SELECT $INGREDIENTS_COLUMN_NAME FROM $INGREDIENTS_TABLE_NAME WHERE $INGREDIENTS_COLUMN_ID = ?"
        val db = readableDatabase
        val cursor = db.rawQuery(query, arrayOf(ingredientId.toString()))
        var ingredientName = ""
        if (cursor.moveToFirst()) {
            ingredientName = cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN_NAME))
        }
        cursor.close()
        return ingredientName
    }

    @SuppressLint("Range")
    fun getIngredientIdByName(ingredientName: String): Int {
        val db = readableDatabase
        val query =
            "SELECT $INGREDIENTS_COLUMN_ID FROM $INGREDIENTS_TABLE_NAME WHERE $INGREDIENTS_COLUMN_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(ingredientName))
        var ingredientId = -1

        if (cursor.moveToFirst()) {
            ingredientId = cursor.getInt(cursor.getColumnIndex(INGREDIENTS_COLUMN_ID))
        }

        cursor.close()
        return ingredientId
    }


    // Add this function to check if an ingredient exists by name
    fun ingredientExistsByName(ingredientName: String): Boolean {
        val query = "SELECT * FROM $INGREDIENTS_TABLE_NAME WHERE $INGREDIENTS_COLUMN_NAME = ?"
        val selectionArgs = arrayOf(ingredientName)
        val cursor = readableDatabase.rawQuery(query, selectionArgs)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // Modify the addIngredient function to return the ingredientId
    fun addIngredient(ingredientName: String): Int {
        if (ingredientExistsByName(ingredientName.lowercase())) {
            return getIngredientIdByName(ingredientName.lowercase())
        }
        val contentValues = ContentValues()
        contentValues.put(INGREDIENTS_COLUMN_NAME, ingredientName.lowercase())
        val newIngredientId =
            writableDatabase.insert(INGREDIENTS_TABLE_NAME, null, contentValues).toInt()
        return newIngredientId
    }












//for showing detailed recipie
    @SuppressLint("Range")
    fun getRecipeIngredients(recipeId: Int): List<RecipeIngredient> {
        val db = this.readableDatabase
        val recipeIngredients = mutableListOf<RecipeIngredient>()

        val projection = arrayOf(
            RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID,
            RECIPE_INGREDIENTS_COLUMN_QUANTITY,
            RECIPE_INGREDIENTS_COLUMN_UNIT,
            RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT
        )

        val selection = "$RECIPE_INGREDIENTS_COLUMN_RECIPE_ID = ?"
        val selectionArgs = arrayOf(recipeId.toString())

        val cursor = db.query(
            RECIPE_INGREDIENTS_TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val ingredientId = getInt(getColumnIndexOrThrow(RECIPE_INGREDIENTS_COLUMN_INGREDIENT_ID))
                val quantity = getFloat(getColumnIndexOrThrow(RECIPE_INGREDIENTS_COLUMN_QUANTITY))
                val unit = getString(getColumnIndexOrThrow(RECIPE_INGREDIENTS_COLUMN_UNIT))
                val isMainIngredient = getInt(getColumnIndexOrThrow(RECIPE_INGREDIENTS_COLUMN_IS_MAIN_INGREDIENT))

                val ingredientName = getIngredientName(ingredientId)

                val recipeIngredient = RecipeIngredient(recipeId, ingredientId, quantity, unit, ingredientName, isMainIngredient)
                recipeIngredients.add(recipeIngredient)
            }
        }

        cursor.close()
        return recipeIngredients
    }






}
/*
    fun deleteAllData() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("DELETE FROM $RECIPES_TABLE_NAME")
            db.execSQL("DELETE FROM $INGREDIENTS_TABLE_NAME")
            db.execSQL("DELETE FROM $RECIPE_INGREDIENTS_TABLE_NAME")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
*/


