package com.example.foodrecommenderapp

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

            val createRecipeIngredientsTableStatement = "CREATE TABLE $RECIPE_INGREDIENTS_TABLE_NAME(" +
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




    fun ingredientExists(ingredientName: String): Boolean {
        val query = "SELECT * FROM $INGREDIENTS_TABLE_NAME WHERE $INGREDIENTS_COLUMN_NAME = ?"
        val selectionArgs = arrayOf(ingredientName)
        val cursor = readableDatabase.rawQuery(query, selectionArgs)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun addIngredient(ingredientName: String): Long {
        if (ingredientExists(ingredientName)) {
            return -1
        }
        val contentValues = ContentValues()
        contentValues.put(INGREDIENTS_COLUMN_NAME, ingredientName)
        return writableDatabase.insert(INGREDIENTS_TABLE_NAME, null, contentValues)
    }


    fun getIngredients(): List<Map<String, String>> {
        val selectQuery = "SELECT $INGREDIENTS_COLUMN_ID, $INGREDIENTS_COLUMN_NAME FROM $INGREDIENTS_TABLE_NAME"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        val ingredients = mutableListOf<Map<String, String>>()
        if (cursor.moveToFirst()) {
            do {
                val ingredient = HashMap<String, String>()
                ingredient["ingredientId"] = cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN_ID))
                ingredient["ingredientName"] = cursor.getString(cursor.getColumnIndex(INGREDIENTS_COLUMN_NAME))
                ingredients.add(ingredient)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredients
    }

}