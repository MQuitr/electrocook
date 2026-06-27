package ru.mquitr.electrocookv2.data.mock

import ru.mquitr.electrocookv2.domain.model.CookingStep
import ru.mquitr.electrocookv2.domain.model.Ingredient
import ru.mquitr.electrocookv2.domain.model.Recipe

object FakeRecipes {

    val recipes = listOf(

        Recipe(
            id = 1,
            title = "Борщ",
            description = "Классический украинский борщ",
            calories = 350,
            proteins = 15.0,
            fats = 12.0,
            carbs = 28.0,
            cookingTime = 90,
            servings = 6,

            ingredients = listOf(
                Ingredient("Свекла", "300 г"),
                Ingredient("Картофель", "400 г"),
                Ingredient("Говядина", "500 г")
            ),

            steps = listOf(
                CookingStep(1, "Отварить мясо."),
                CookingStep(2, "Подготовить овощи."),
                CookingStep(3, "Добавить овощи в бульон."),
                CookingStep(4, "Варить 30 минут.")
            ),

            allergens = listOf()
        ),

        Recipe(
            id = 2,
            title = "Карбонара",
            description = "Паста карбонара",
            calories = 520,
            proteins = 20.0,
            fats = 18.0,
            carbs = 60.0,
            cookingTime = 25,
            servings = 2,

            ingredients = listOf(
                Ingredient("Спагетти", "250 г"),
                Ingredient("Бекон", "150 г"),
                Ingredient("Яйца", "2 шт")
            ),

            steps = listOf(
                CookingStep(1, "Отварить пасту."),
                CookingStep(2, "Обжарить бекон."),
                CookingStep(3, "Смешать ингредиенты.")
            ),

            allergens = listOf(
                "Яйца",
                "Глютен"
            )
        )
    )
}