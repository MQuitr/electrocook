package ru.mquitr.electrocookv2.data.mock

import ru.mquitr.electrocookv2.domain.model.RecipePackage

object FakePackages {

    val packages = listOf(

        RecipePackage(
            id = "italian",
            title = "Итальянская кухня",
            description = "Паста, пицца, ризотто и другие блюда итальянской кухни.",
            recipeCount = 25,
            sizeBytes = 2_500_000L
        ),

        RecipePackage(
            id = "breakfast",
            title = "Завтраки",
            description = "Простые и вкусные блюда для начала дня.",
            recipeCount = 30,
            sizeBytes = 1_800_000L,
            isInstalled = true
        ),

        RecipePackage(
            id = "desserts",
            title = "Десерты",
            description = "Торты, пирожные, печенье и другие сладости.",
            recipeCount = 40,
            sizeBytes = 3_200_000L
        ),

        RecipePackage(
            id = "soups",
            title = "Супы",
            description = "Традиционные и современные рецепты супов.",
            recipeCount = 20,
            sizeBytes = 1_500_000L
        ),

        RecipePackage(
            id = "healthy",
            title = "Здоровое питание",
            description = "Сбалансированные блюда на каждый день.",
            recipeCount = 35,
            sizeBytes = 2_700_000L
        )
    )
}
