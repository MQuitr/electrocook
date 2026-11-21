
package ru.mquitr.electrocookv2.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.mquitr.electrocookv2.data.local.LocalRecipesRepository
import ru.mquitr.electrocookv2.presentation.components.RecipeList

@Composable
fun HomeScreen(
    navController: NavHostController,
    searchQuery: String
) {

    val context = LocalContext.current

    val repository = remember(context) {
        LocalRecipesRepository(context)
    }

    // Реальные установленные рецепты из Room.
    val recipes by remember(repository) {
        repository.observeRecipes()
    }.collectAsState(initial = emptyList())

    val filteredRecipes = recipes.filter { recipe ->
        recipe.title.contains(
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
            text = if (searchQuery.isBlank()) {
                "Установленные рецепты"
            } else {
                "Найдено: ${filteredRecipes.size}"
            },
            style = MaterialTheme.typography.titleLarge
        )

        if (filteredRecipes.isEmpty()) {

            Text(
                text = when {
                    recipes.isEmpty() ->
                        "Пока нет установленных рецептов"

                    else ->
                        "Ничего не найдено"
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = when {
                    recipes.isEmpty() ->
                        "Откройте раздел «Пакеты рецептов» " +
                                "и установите любой пакет."

                    else ->
                        "Попробуйте изменить поисковый запрос."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

        } else {

            RecipeList(
                recipes = filteredRecipes,
                onRecipeClick = { recipeId ->
                    navController.navigate(
                        "recipe/$recipeId"
                    )
                }
            )
        }
    }
}
