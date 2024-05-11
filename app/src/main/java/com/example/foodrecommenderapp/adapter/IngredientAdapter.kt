package com.example.myapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.Ingredient
import com.example.foodrecommenderapp.R
import com.example.foodrecommenderapp.RecipeIngredient
import com.example.foodrecommenderapp.DatabaseHelper

class IngredientAdapter(private val ingredients: MutableList<RecipeIngredient>, private val context: Context, private val ingredientList: List<Ingredient>, private val databaseHandler: DatabaseHelper) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View, private val adapter: IngredientAdapter) : RecyclerView.ViewHolder(itemView) {
        val ingredientNameEditText: EditText = itemView.findViewById(R.id.ingredient_name)
        val ingredientQuantityEditText: EditText = itemView.findViewById(R.id.ingredient_quantity)
        val ingredientUnitSpinner: Spinner = itemView.findViewById(R.id.ingredient_unit)
        val ingredientTypeRadioGroup: RadioGroup = itemView.findViewById(R.id.ingredient_type_group)
        val addIngredientButton: Button = itemView.findViewById(R.id.save_ingredient_button)

        init {
            addIngredientButton.setOnClickListener {
                adapter.addIngredientName(adapter, this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view, this)
    }
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val recipeIngredient = ingredients[position]
        val ingredient = ingredientList.find { it.ingredientId == recipeIngredient.ingredientId }
        if (ingredient != null) {
            holder.ingredientNameEditText.setText(ingredient.ingredientName)
        }
        holder.ingredientQuantityEditText.setText(recipeIngredient.quantity.toString())
        holder.ingredientUnitSpinner.setSelection(getUnitPosition(context, recipeIngredient.unit))
        if (recipeIngredient.isMainIngredient == 1) {
            holder.ingredientTypeRadioGroup.check(R.id.ingredient_type_main)
        } else {
            holder.ingredientTypeRadioGroup.check(R.id.ingredient_type_side)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun addNewIngredient() {
        ingredients.add(RecipeIngredient(0, 0, 0f, "", 0))
        notifyItemInserted(ingredients.size - 1)
    }

    private fun getUnitPosition(context: Context, unit: String): Int {
        val units = context.resources.getStringArray(R.array.ingredient_units)
        return units.indexOf(unit)
    }

    fun addIngredientName(adapter: IngredientAdapter, holder: IngredientViewHolder) {
        val ingredientName = holder.ingredientNameEditText.text.toString()
        if (ingredientName.isNotEmpty()) {
            val ingredientId = databaseHandler.addIngredient(ingredientName)
            if (ingredientId != -1L) {
                val recipeIngredient = RecipeIngredient(ingredientId.toInt(), 0, 0f, ingredientName, 0)
                adapter.ingredients.add(recipeIngredient)
                adapter.notifyItemInserted(adapter.ingredients.size - 1)
            }
        }
    }
}