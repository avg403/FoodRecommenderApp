package com.example.foodrecommenderapp

class Recipe(
    val recipeId: Int,
    val recipeName: String,
    val instructions: String,
    val imageFilePath: String,
    val cookingTime: Int,
    val servings: Int,
    val cuisine: String
) {
    constructor(recipeName: String, instructions: String, imageFilePath: String, cookingTime: Int, servings: Int, cuisine: String)
            : this(0, recipeName, instructions, imageFilePath, cookingTime, servings, cuisine)
}

class Ingredient(
    val ingredientId: Int,
    val ingredientName: String
)

data class RecipeIngredient(
    var recipeId: Int = 0,
    var ingredientId: Int = 0,
    var quantity: Float = 0f,
    var unit: String = "",
    var ingredientName: String = "",
    var isMainIngredient: Int = 0
)