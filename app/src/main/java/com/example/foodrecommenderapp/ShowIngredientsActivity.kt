package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton

class ShowIngredientsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_ingredient)



        val backButton = findViewById<AppCompatImageButton>(R.id.back_button6)
        backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }


        databaseHelper = DatabaseHelper(this)

        val ingredients = databaseHelper.getIngredients()

        val ingredientListTextView = TextView(this)
        ingredientListTextView.text = ingredients.joinToString("\n") {
            "${it["ingredientId"]}. ${it["ingredientName"]}"
        }
        ingredientListTextView.setPadding(16, 16, 16, 16)
        ingredientListTextView.textSize = 24f

        val showIngredientContainer = findViewById<LinearLayout>(R.id.showIngredientContainer)

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 16, 16, 16)

        ingredientListTextView.layoutParams = params

        showIngredientContainer.addView(ingredientListTextView)
    }
}