
package ru.mquitr.electrocookv2.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mquitr.electrocookv2.domain.model.CookingStep
import ru.mquitr.electrocookv2.domain.model.Ingredient
import ru.mquitr.electrocookv2.domain.model.Recipe

class LocalRecipesRepository(context: Context) {

    private val dao = ElectroCookDatabase
        .getInstance(context.applicationContext)
        .recipesDao()

    // Список рецептов для карточек.
    // Обновляется автоматически при изменении Room.
    fun observeRecipes(): Flow<List<Recipe>> {
        return dao.observeRecipes().map { entities ->
            entities.map { entity ->
                entity.toDomainRecipe()
            }
        }
    }

    // Полный рецепт для страницы подробностей.
    suspend fun getRecipeById(id: Int): Recipe? {

        val entity = dao.getRecipeById(id)
            ?: return null

        val ingredients = dao
            .getIngredientsForRecipe(id)
            .map { item ->
                Ingredient(
                    name = item.name,
                    amount = item.amount
                )
            }

        val steps = dao
            .getStepsForRecipe(id)
            .map { item ->
                CookingStep(
                    stepNumber = item.stepNumber,
                    description = item.description
                )
            }

        val allergens = dao
            .getAllergensForRecipe(id)
            .map { item ->
                item.allergen
            }

        return entity.toDomainRecipe(
            ingredients = ingredients,
            steps = steps,
            allergens = allergens
        )
    }

    private fun RecipeEntity.toDomainRecipe(
        ingredients: List<Ingredient> = emptyList(),
        steps: List<CookingStep> = emptyList(),
        allergens: List<String> = emptyList()
    ): Recipe {

        return Recipe(
            id = id,
            title = title,
            description = description,
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs,
            cookingTime = cookingTime,
            servings = servings,
            imageUrl = null,
            ingredients = ingredients,
            steps = steps,
            allergens = allergens
        )
    }
}
