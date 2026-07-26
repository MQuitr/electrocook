package ru.mquitr.electrocookv2.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import androidx.navigation.NavHostController
import ru.mquitr.electrocookv2.presentation.components.RecipeList

@Composable
fun HomeScreen(
    navController: NavHostController,
    searchQuery: String
) {

    val filteredRecipes = FakeRecipes.recipes.filter {

        it.title.contains(
            searchQuery,
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = if (searchQuery.isBlank())
                "Популярные рецепты"
            else
                "Найдено: ${filteredRecipes.size}",
        )

        if (filteredRecipes.isEmpty()) {

            Text(
                text = "Ничего не найдено",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "Попробуйте изменить поисковый запрос",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

        } else {

            RecipeList(
                recipes = filteredRecipes,
                onRecipeClick = { recipeId ->
                    navController.navigate("recipe/$recipeId")
                }
            )
        }
    }
}