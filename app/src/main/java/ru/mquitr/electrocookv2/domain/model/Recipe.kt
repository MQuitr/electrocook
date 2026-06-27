package ru.mquitr.electrocookv2.domain.model

data class Recipe(

    val id: Int,
    val title: String,
    val description: String,
    val calories: Int,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val cookingTime: Int,
    val servings: Int,
    val imageUrl: String? = null,
    val ingredients: List<Ingredient>,
    val steps: List<CookingStep>,
    val allergens: List<String>
)