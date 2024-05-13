package com.example.myapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommenderapp.Ingredient
import com.example.foodrecommenderapp.R
import com.example.foodrecommenderapp.RecipeIngredient
import com.example.foodrecommenderapp.DatabaseHelper
class IngredientAdapter(private val ingredients: MutableList<RecipeIngredient>, private val context: Context, private val ingredientList: List<Ingredient>, private val databaseHandler: DatabaseHelper) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View, private val adapter: IngredientAdapter) :
        RecyclerView.ViewHolder(itemView) {
        val ingredientNameEditText: EditText = itemView.findViewById(R.id.ingredient_name)
        val ingredientQuantityEditText: EditText = itemView.findViewById(R.id.ingredient_quantity)
        val ingredientUnitEditText: EditText = itemView.findViewById(R.id.ingredient_unit)
        val ingredientTypeRadioGroup: RadioGroup = itemView.findViewById(R.id.ingredient_type_group)
        val deleteIngredientButton: ImageButton =
            itemView.findViewById(R.id.delete_ingredient_button)

        init {
            deleteIngredientButton.setOnClickListener {
                // Delete the ingredient at the current position
                adapter.ingredients.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val recipeIngredient = ingredients[position]
        holder.ingredientNameEditText.setText(recipeIngredient.ingredientName)
        holder.ingredientQuantityEditText.setText(recipeIngredient.quantity.toString())
        holder.ingredientUnitEditText.setText(recipeIngredient.unit)
        if (recipeIngredient.isMainIngredient == 1) {
            holder.ingredientTypeRadioGroup.check(R.id.ingredient_type_main)
        } else {
            holder.ingredientTypeRadioGroup.check(R.id.ingredient_type_side)
        }

        // Set up edit text focus change listeners
        holder.ingredientNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                addIngredientName(this, holder)
            }
        }

        holder.ingredientQuantityEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateIngredientQuantity(this, holder)
            }
        }

        holder.ingredientUnitEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateIngredientUnit(this, holder)
            }
        }

        holder.ingredientTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateIsMainIngredient(this, holder, checkedId)
        }


    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun addNewIngredient() {
        ingredients.add(RecipeIngredient())
        notifyItemInserted(ingredients.size - 1)
    }

    private fun addIngredientName(adapter: IngredientAdapter, holder: IngredientViewHolder) {
        val ingredientName = holder.ingredientNameEditText.text.toString()
        val ingredientQuantity = holder.ingredientQuantityEditText.text.toString().toFloatOrNull() ?: 0f
        val ingredientUnit = holder.ingredientUnitEditText.text.toString()
        val isMainIngredient = when (holder.ingredientTypeRadioGroup.checkedRadioButtonId) {
            R.id.ingredient_type_main -> 1
            R.id.ingredient_type_side -> 0
            else -> 0
        }

        if (ingredientName.isNotEmpty()) {
            val ingredientId = databaseHandler.addIngredient(ingredientName)
            if (ingredientId != -1) {
                val recipeIngredient = RecipeIngredient(
                    ingredientId = ingredientId,
                    quantity = ingredientQuantity,
                    unit = ingredientUnit,
                    ingredientName = ingredientName,
                    isMainIngredient = isMainIngredient
                )
                adapter.ingredients[holder.adapterPosition] = recipeIngredient
                adapter.notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    fun updateRecipeId(recipeId: Int) {
        for (recipeIngredient in ingredients) {
            recipeIngredient.recipeId = recipeId
        }
    }


    private fun updateIngredientQuantity(adapter: IngredientAdapter, holder: IngredientViewHolder) {
        val quantity = holder.ingredientQuantityEditText.text.toString().toFloatOrNull() ?: 0f
        adapter.ingredients[holder.adapterPosition].quantity = quantity
    }

    private fun updateIngredientUnit(adapter: IngredientAdapter, holder: IngredientViewHolder) {
        val unit = holder.ingredientUnitEditText.text.toString()
        adapter.ingredients[holder.adapterPosition].unit = unit
    }

    private fun updateIsMainIngredient(adapter: IngredientAdapter, holder: IngredientViewHolder, checkedId: Int) {
        val isMainIngredient = when (checkedId) {
            R.id.ingredient_type_main -> 1
            R.id.ingredient_type_side -> 0
            else -> 0
        }
        adapter.ingredients[holder.adapterPosition].isMainIngredient = isMainIngredient
    }

}