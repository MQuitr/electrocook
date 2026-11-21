
package ru.mquitr.electrocookv2.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithDetails(

    @Embedded
    val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<CookingStepEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val allergens: List<AllergenEntity>
)
