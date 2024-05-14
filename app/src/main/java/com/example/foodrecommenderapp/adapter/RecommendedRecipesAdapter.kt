package com.example.foodrecommenderapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.R
import com.example.foodrecommenderapp.Recipe
import java.io.File

class RecommendedRecipesAdapter(private val onRecipeClickListener: (Recipe) -> Unit) : RecyclerView.Adapter<RecommendedRecipesAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = emptyList()

    fun setRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)

        init {
            itemView.setOnClickListener {
                val recipe = recipes[adapterPosition]
                onRecipeClickListener(recipe)
            }
        }

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.recipeName

            // Load the recipe image from the file path
            val imageFile = File(recipe.imageFilePath)
            if (imageFile.exists()) {
                val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                recipeImageView.setImageBitmap(imageBitmap)
            } else {
                // Handle the case when no image is available
                recipeImageView.setImageResource(R.drawable.joeydoesntsharefood) // Replace with your default image resource
            }
        }
    }
}