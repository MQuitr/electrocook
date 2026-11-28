
package ru.mquitr.electrocookv2.domain.model

data class RecipePackage(
    val id: String,
    val title: String,
    val description: String,
    val recipeCount: Int,
    val sizeBytes: Long? = null,
    val version: Int = 1
)
