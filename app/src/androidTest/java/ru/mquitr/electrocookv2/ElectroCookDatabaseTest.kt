
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

    @Test
    fun packageCanBeInstalledAndDeleted() = runBlocking {

        val context =
            InstrumentationRegistry.getInstrumentation()
                .targetContext

        val database = Room.inMemoryDatabaseBuilder(
            context,
            ElectroCookDatabase::class.java
        ).build()

        try {

            val dao = database.recipesDao()

            dao.replacePackageData(
                recipePackage = InstalledPackageEntity(
                    id = "italian",
                    title = "Итальянская кухня",
                    description = "Тестовый пакет",
                    version = 1
                ),

                recipes = listOf(
                    RecipeEntity(
                        id = 1001,
                        packageId = "italian",
                        title = "Паста карбонара",
                        description = "Тестовый рецепт",
                        calories = 650,
                        proteins = 28.0,
                        fats = 32.0,
                        carbs = 62.0,
                        cookingTime = 25,
                        servings = 2,
                        imagePath = "images/1001.webp"
                    )
                ),

                ingredients = listOf(
                    IngredientEntity(
                        recipeId = 1001,
                        position = 1,
                        name = "Спагетти",
                        amount = "200 г"
                    )
                ),

                steps = listOf(
                    CookingStepEntity(
                        recipeId = 1001,
                        stepNumber = 1,
                        description = "Отварить спагетти.",
                        imagePath = null
                    )
                ),

                allergens = listOf(
                    AllergenEntity(
                        recipeId = 1001,
                        allergen = "Глютен"
                    )
                )
            )

            assertEquals(
                1,
                dao.observeInstalledPackages().first().size
            )

            assertEquals(
                "Паста карбонара",
                dao.getRecipeById(1001)?.title
            )

            assertEquals(
                1,
                dao.getIngredientsForRecipe(1001).size
            )

            assertEquals(
                1,
                dao.getStepsForRecipe(1001).size
            )

            assertEquals(
                1,
                dao.getAllergensForRecipe(1001).size
            )

            dao.deletePackage("italian")

            assertNull(dao.getRecipeById(1001))

            assertTrue(
                dao.getIngredientsForRecipe(1001).isEmpty()
            )

            assertTrue(
                dao.getStepsForRecipe(1001).isEmpty()
            )

            assertTrue(
                dao.getAllergensForRecipe(1001).isEmpty()
            )

        } finally {
            database.close()
        }
    }
}
