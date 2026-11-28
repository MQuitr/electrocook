
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
import ru.mquitr.electrocookv2.data.local.ElectroCookDatabase
import ru.mquitr.electrocookv2.data.local.InstalledPackageEntity
import ru.mquitr.electrocookv2.data.local.RecipeEntity

@RunWith(AndroidJUnit4::class)
class PackageTransactionTest {

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
        version: Int
    ) = InstalledPackageEntity(
        id = "italian",
        title = "Итальянская кухня",
        description = "Тестовый пакет",
        version = version
    )

    private fun testRecipe(
        id: Int
    ) = RecipeEntity(
        id = id,
        packageId = "italian",
        title = "Рецепт $id",
        description = "Тестовое описание",
        calories = 350,
        proteins = 15.0,
        fats = 12.0,
        carbs = 28.0,
        cookingTime = 30,
        servings = 2,
        imagePath = null
    )

    @Test
    fun failedReplacementPreservesOriginalPackage() = runBlocking {

        val database = createDatabase()

        try {
            val dao = database.recipesDao()

            // Существующий пакет версии 1
            dao.replacePackageData(
                recipePackage = testPackage(1),
                recipes = listOf(testRecipe(1001)),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            var replacementFailed = false

            try {
                // Два рецепта с одинаковым ID.
                // Вставка должна нарушить PRIMARY KEY.
                dao.replacePackageData(
                    recipePackage = testPackage(2),
                    recipes = listOf(
                        testRecipe(1006),
                        testRecipe(1006)
                    ),
                    ingredients = emptyList(),
                    steps = emptyList(),
                    allergens = emptyList()
                )

            } catch (_: Exception) {
                replacementFailed = true
            }

            assertTrue(
                "Invalid replacement must fail",
                replacementFailed
            )

            // Старый рецепт должен сохраниться
            assertEquals(
                1001,
                dao.getRecipeWithDetailsById(1001)
                    ?.recipe?.id
            )

            // Новый рецепт не должен появиться
            assertNull(
                dao.getRecipeWithDetailsById(1006)
            )

            // Версия пакета тоже не должна измениться
            val installed = dao
                .observeInstalledPackages()
                .first()
                .single()

            assertEquals(1, installed.version)

            assertEquals(
                1,
                dao.observeRecipes().first().size
            )

        } finally {
            database.close()
        }
    }

    @Test
    fun invalidIngredientReferencePreservesData() = runBlocking {

        val database = createDatabase()

        try {
            val dao = database.recipesDao()

            dao.replacePackageData(
                recipePackage = testPackage(1),
                recipes = listOf(testRecipe(1001)),
                ingredients = emptyList(),
                steps = emptyList(),
                allergens = emptyList()
            )

            var replacementFailed = false

            try {
                dao.replacePackageData(
                    recipePackage = testPackage(2),
                    recipes = listOf(testRecipe(1006)),
                    ingredients = listOf(
                        ru.mquitr.electrocookv2.data.local.IngredientEntity(
                            recipeId = 9999,
                            position = 1,
                            name = "Некорректный ингредиент",
                            amount = "1 шт."
                        )
                    ),
                    steps = emptyList(),
                    allergens = emptyList()
                )

            } catch (_: IllegalArgumentException) {
                replacementFailed = true
            }

            assertTrue(replacementFailed)

            // Проверяем сохранность первоначальных данных
            assertEquals(
                1001,
                dao.getRecipeWithDetailsById(1001)
                    ?.recipe?.id
            )

            assertNull(
                dao.getRecipeWithDetailsById(1006)
            )

            assertEquals(
                1,
                dao.observeInstalledPackages()
                    .first()
                    .single()
                    .version
            )

        } finally {
            database.close()
        }
    }
}
