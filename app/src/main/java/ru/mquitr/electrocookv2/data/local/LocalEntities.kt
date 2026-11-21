
package ru.mquitr.electrocookv2.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "installed_packages")
data class InstalledPackageEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val version: Int
)

@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = InstalledPackageEntity::class,
            parentColumns = ["id"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packageId")]
)
data class RecipeEntity(
    @PrimaryKey
    val id: Int,
    val packageId: String,
    val title: String,
    val description: String,
    val calories: Int,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val cookingTime: Int,
    val servings: Int,
    val imagePath: String?
)

@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipeId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    val recipeId: Int,
    val position: Int,
    val name: String,
    val amount: String
)

@Entity(
    tableName = "recipe_steps",
    primaryKeys = ["recipeId", "stepNumber"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CookingStepEntity(
    val recipeId: Int,
    val stepNumber: Int,
    val description: String,
    val imagePath: String?
)

@Entity(
    tableName = "recipe_allergens",
    primaryKeys = ["recipeId", "allergen"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AllergenEntity(
    val recipeId: Int,
    val allergen: String
)
