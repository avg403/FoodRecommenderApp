package com.example.foodrecommenderapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.R
import com.example.foodrecommenderapp.Recipe

class RecipeSearchResultsAdapter(private val onRecipeClickListener: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeSearchResultsAdapter.ViewHolder>() {

    private var recipes: List<Recipe> = emptyList()

    fun setRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)

        init {
            itemView.setOnClickListener {
                val recipe = recipes[adapterPosition]
                onRecipeClickListener(recipe)
            }
        }

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.recipeName
        }
    }
}