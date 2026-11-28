
package ru.mquitr.electrocookv2.data.local

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.mquitr.electrocookv2.domain.model.CookingStep
import ru.mquitr.electrocookv2.domain.model.Ingredient
import ru.mquitr.electrocookv2.domain.model.Recipe
import java.io.File

class LocalRecipesRepository(context: Context) {

    private val appContext = context.applicationContext

    private val dao = ElectroCookDatabase
        .getInstance(appContext)
        .recipesDao()

    private val packageIdPattern =
        Regex("[a-z0-9_-]+")

    private val imagePathPattern =
        Regex("images/[A-Za-z0-9_-]+\\.(webp|png|jpg|jpeg)")

    // Список рецептов для главной и избранного.
    fun observeRecipes(): Flow<List<Recipe>> {

        return dao.observeRecipes().map { entities ->
            entities.map { it.toDomainRecipe() }
        }
    }

    // Полные данные для поиска и фильтров.
    fun observeRecipesWithDetails(): Flow<List<Recipe>> {

        return dao.observeRecipesWithDetails().map { items ->
            items.map { it.toDomainRecipe() }
        }
    }

    // Подробности отдельного рецепта.
    suspend fun getRecipeById(id: Int): Recipe? {

        return dao.getRecipeWithDetailsById(id)
            ?.toDomainRecipe()
    }

    // Единый преобразователь полного рецепта.
    private fun RecipeWithDetails.toDomainRecipe(): Recipe {

        val recipeIngredients = ingredients
            .sortedBy { it.position }
            .map { ingredient ->
                Ingredient(
                    name = ingredient.name,
                    amount = ingredient.amount
                )
            }

        val recipeSteps = steps
            .sortedBy { it.stepNumber }
            .map { step ->
                CookingStep(
                    stepNumber = step.stepNumber,
                    description = step.description,
                    imageUrl = getLocalImageUri(
                        recipe.packageId,
                        step.imagePath
                    )
                )
            }

        val recipeAllergens = allergens
            .map { it.allergen }

        return recipe.toDomainRecipe(
            ingredients = recipeIngredients,
            steps = recipeSteps,
            allergens = recipeAllergens
        )
    }

    // Преобразование основной записи Room.
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

            imageUrl = getLocalImageUri(
                packageId,
                imagePath
            ),

            ingredients = ingredients,
            steps = steps,
            allergens = allergens
        )
    }

    // Безопасное получение локальной фотографии.
    private fun getLocalImageUri(
        packageId: String,
        imagePath: String?
    ): String? {

        if (imagePath == null) {
            return null
        }

        if (!packageIdPattern.matches(packageId) ||
            !imagePathPattern.matches(imagePath)
        ) {
            return null
        }

        val packageDirectory = File(
            File(appContext.filesDir, "recipe_packages"),
            packageId
        )

        val imageFile = File(
            packageDirectory,
            imagePath
        )

        if (!imageFile.isFile) {
            return null
        }

        return Uri.fromFile(imageFile).toString()
    }
}
