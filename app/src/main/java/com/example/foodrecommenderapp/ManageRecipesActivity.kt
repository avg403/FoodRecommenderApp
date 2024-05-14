package com.example.foodrecommenderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ManageRecipesActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recipeAdapter: RecipeAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_recipes)


        val backButton = findViewById<ImageButton>(R.id.back_button10)
        backButton.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }

        databaseHelper = DatabaseHelper(this)
        val recipes = databaseHelper.getAllRecipes()

        recipeAdapter = RecipeAdapter(recipes)

        val recyclerView = findViewById<RecyclerView>(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter
    }

    private inner class RecipeAdapter(recipes: List<Recipe>) :
        RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

        private val recipesList = recipes.toMutableList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_manage_recipe, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val recipe = recipesList[position]
            holder.bind(recipe)
        }

        override fun getItemCount(): Int = recipesList.size

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
            private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
            private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

            fun bind(recipe: Recipe) {
                recipeNameTextView.text = recipe.recipeName

                // Load the recipe image from the file path
                val imageFile = File(recipe.imageFilePath)
                if (imageFile.exists()) {
                    val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    recipeImageView.setImageBitmap(imageBitmap)
                } else {
                    // Handle the case when no image is available
                    recipeImageView.setImageResource(R.drawable.joeydoesntsharefood)
                }

                deleteButton.setOnClickListener {
                    databaseHelper.deleteRecipe(recipe.recipeId)
                    recipesList.remove(recipe)
                    recipeAdapter.notifyDataSetChanged()
                }

                itemView.setOnClickListener {
                    val intent =
                        Intent(this@ManageRecipesActivity, RecipeDetailsActivity::class.java)
                    intent.putExtra("recipe", recipe)
                    startActivity(intent)
                }
            }
        }
    }
}