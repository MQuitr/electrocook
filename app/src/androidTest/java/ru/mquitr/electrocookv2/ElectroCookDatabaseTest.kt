
package ru.mquitr.electrocookv2

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.mquitr.electrocookv2.data.local.*

@RunWith(AndroidJUnit4::class)
class ElectroCookDatabaseTest {

    private fun createDatabase(): ElectroCookDatabase {

        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        return Room.inMemoryDatabaseBuilder(
            context,
            ElectroCookDatabase::class.java
        ).build()
    }

    private fun testPackage(
        id: String,
        version: Int = 1
    ) = InstalledPackageEntity(
        id = id,
        title = id,
        description = "Тестовый пакет",
        version = version
    )

    private fun testRecipe(
        id: Int,
        packageId: String
    ) = RecipeEntity(
        id = id,
        packageId = packageId,
        title = "Рецепт $id",
        description = "Тестовое описание",
        calories = 350,
        proteins = 15.0,
        fats = 12.0,
        carbs = 28.0,
        cookingTime = 30,
        servings = 2,
        imagePath = "images/$id.webp"
    )

    @Test
    fun recipeRelationsAreLoadedAndDeleted() = runBlocking {

        val database = createDatabase()

        try {
            val dao = database.recipesDao()

            dao.replacePackageData(
                recipePackage = testPackage("italian"),
                recipes = listOf(
                    testRecipe(1001, "italian")
                ),
                ingredients = listOf(
                    IngredientEntity(
                        recipeId = 1001,
                        position = 1,
                        name = "Спагетти",
                        amount = "200 г"
                    ),
                    IngredientEntity(
                        recipeId = 1001,
                        position = 2,
                        name = "Яйца",
                        amount = "2 шт."
                    )
                ),
                steps = listOf(
                    CookingStepEntity(
                        recipeId = 1001,
                        stepNumber = 1,
                        description = "Отварить спагетти",
                        imagePath = null
                    ),
                    CookingStepEntity(
                        recipeId = 1001,
                        stepNumber = 2,
                        description = "Смешать ингредиенты",
                        imagePath = "images/1001-step2.webp"
                    )
                ),
                allergens = listOf(
                    AllergenEntity(
                        recipeId = 1001,
                        allergen = "Глютен"
                    )
                )
            )

            val recipe = requireNotNull(
                dao.getRecipeWithDetailsById(1001)
            )

            assertEquals(1001, recipe.recipe.id)
            assertEquals(2, recipe.ingredients.size)
            assertEquals(2, recipe.steps.size)
            assertEquals(1, recipe.allergens.size)

            assertEquals(
                "images/1001-step2.webp",
                recipe.steps.first {
                    it.stepNumber == 2
                }.imagePath
            )

            dao.deletePackage("italian")

            assertNull(
                dao.getRecipeWithDetailsById(1001)
            )

            assertTrue(
                dao.observeRecipesWithDetails()
                    .first()
                    .isEmpty()
            )

        } finally {
            database.close()
        }
    }

    @Test
    fun deletingOnePackageKeepsOtherRecipes() = runBlocking {

        val database = createDatabase()

        try {
            val dao = database.recipesDao()

            dao.replacePackageData(
                recipePackage = testPackage("italian"),
                recipes = listOf(
                    testRecipe(1001, "italian")
                ),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            dao.replacePackageData(
                recipePackage = testPackage("breakfast"),
                recipes = listOf(
                    testRecipe(1002, "breakfast")
                ),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            dao.deletePackage("italian")

            assertNull(
                dao.getRecipeWithDetailsById(1001)
            )

            assertEquals(
                1002,
                dao.getRecipeWithDetailsById(1002)
                    ?.recipe?.id
            )

            assertEquals(
                listOf("breakfast"),
                dao.observeInstalledPackages()
                    .first()
                    .map { it.id }
            )

        } finally {
            database.close()
        }
    }

    @Test
    fun replacingPackageRemovesOldRecipeData() = runBlocking {

        val database = createDatabase()

        try {
            val dao = database.recipesDao()

            dao.replacePackageData(
                recipePackage = testPackage(
                    "italian",
                    version = 1
                ),
                recipes = listOf(
                    testRecipe(1001, "italian")
                ),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            dao.replacePackageData(
                recipePackage = testPackage(
                    "italian",
                    version = 2
                ),
                recipes = listOf(
                    testRecipe(1006, "italian")
                ),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            assertNull(
                dao.getRecipeWithDetailsById(1001)
            )

            assertEquals(
                1006,
                dao.getRecipeWithDetailsById(1006)
                    ?.recipe?.id
            )

            assertEquals(
                2,
                dao.observeInstalledPackages()
                    .first()
                    .single()
                    .version
            )

            assertEquals(
                1,
                dao.observeRecipes().first().size
            )

        } finally {
            database.close()
        }
    }
}
