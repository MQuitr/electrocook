
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

    // Преобразует относительный путь из Room
    // в адрес локального файла Android.
    private fun getLocalImageUri(
        packageId: String,
        imagePath: String?
    ): String? {

        if (imagePath == null) {
            return null
        }

        // Не допускаем произвольные пути
        // за пределами каталога пакета.
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

    // Главная страница и избранное.
    fun observeRecipes(): Flow<List<Recipe>> {

        return dao.observeRecipes().map { entities ->
            entities.map { entity ->
                entity.toDomainRecipe()
            }
        }
    }

    // Поиск с ингредиентами и аллергенами.
    fun observeRecipesWithDetails(): Flow<List<Recipe>> {

        return dao.observeRecipesWithDetails().map { items ->

            items.map { item ->

                val ingredients = item.ingredients
                    .sortedBy { it.position }
                    .map { ingredient ->
                        Ingredient(
                            name = ingredient.name,
                            amount = ingredient.amount
                        )
                    }

                val steps = item.steps
                    .sortedBy { it.stepNumber }
                    .map { step ->
                        CookingStep(
                            stepNumber = step.stepNumber,
                            description = step.description,
                            imageUrl = getLocalImageUri(
                                item.recipe.packageId,
                                step.imagePath
                            )
                        )
                    }

                val allergens = item.allergens
                    .map { it.allergen }

                item.recipe.toDomainRecipe(
                    ingredients = ingredients,
                    steps = steps,
                    allergens = allergens
                )
            }
        }
    }

    // Полный рецепт для экрана подробностей.
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
                    description = item.description,
                    imageUrl = getLocalImageUri(
                        entity.packageId,
                        item.imagePath
                    )
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

            imageUrl = getLocalImageUri(
                packageId,
                imagePath
            ),

            ingredients = ingredients,
            steps = steps,
            allergens = allergens
        )
    }
}
