
package ru.mquitr.electrocookv2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipesDao {

    @Query("SELECT * FROM installed_packages ORDER BY title")
    abstract fun observeInstalledPackages():
            Flow<List<InstalledPackageEntity>>

    @Query("SELECT * FROM recipes ORDER BY title")
    abstract fun observeRecipes():
            Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    abstract suspend fun getRecipeById(
        id: Int
    ): RecipeEntity?

    @Query(
        "SELECT * FROM recipe_ingredients " +
                "WHERE recipeId = :recipeId ORDER BY position"
    )
    abstract suspend fun getIngredientsForRecipe(
        recipeId: Int
    ): List<IngredientEntity>

    @Query(
        "SELECT * FROM recipe_steps " +
                "WHERE recipeId = :recipeId ORDER BY stepNumber"
    )
    abstract suspend fun getStepsForRecipe(
        recipeId: Int
    ): List<CookingStepEntity>

    @Query(
        "SELECT * FROM recipe_allergens " +
                "WHERE recipeId = :recipeId ORDER BY allergen"
    )
    abstract suspend fun getAllergensForRecipe(
        recipeId: Int
    ): List<AllergenEntity>

    @Upsert
    abstract suspend fun upsertPackage(
        recipePackage: InstalledPackageEntity
    )

    @Query("DELETE FROM recipes WHERE packageId = :packageId")
    abstract suspend fun deleteRecipesForPackage(
        packageId: String
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertRecipes(
        recipes: List<RecipeEntity>
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertIngredients(
        ingredients: List<IngredientEntity>
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertSteps(
        steps: List<CookingStepEntity>
    )

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertAllergens(
        allergens: List<AllergenEntity>
    )

    @Query("DELETE FROM installed_packages WHERE id = :packageId")
    abstract suspend fun deletePackage(
        packageId: String
    )

    @Transaction
    open suspend fun replacePackageData(
        recipePackage: InstalledPackageEntity,
        recipes: List<RecipeEntity>,
        ingredients: List<IngredientEntity>,
        steps: List<CookingStepEntity>,
        allergens: List<AllergenEntity>
    ) {

        require(recipes.all { it.packageId == recipePackage.id }) {
            "Recipe package ID mismatch"
        }

        val recipeIds = recipes.map { it.id }.toSet()

        require(ingredients.all { it.recipeId in recipeIds }) {
            "Ingredient recipe ID mismatch"
        }

        require(steps.all { it.recipeId in recipeIds }) {
            "Cooking step recipe ID mismatch"
        }

        require(allergens.all { it.recipeId in recipeIds }) {
            "Allergen recipe ID mismatch"
        }

        upsertPackage(recipePackage)
        deleteRecipesForPackage(recipePackage.id)

        insertRecipes(recipes)
        insertIngredients(ingredients)
        insertSteps(steps)
        insertAllergens(allergens)
    }
}
