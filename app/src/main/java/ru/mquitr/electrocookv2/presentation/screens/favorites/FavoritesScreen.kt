
package ru.mquitr.electrocookv2.presentation.screens.favorites

import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.NavController
import ru.mquitr.electrocookv2.data.local.LocalRecipesRepository
import ru.mquitr.electrocookv2.data.preferences.FavoritesPreferences
import ru.mquitr.electrocookv2.presentation.components.RecipeList

@Composable
fun FavoritesScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val repository = remember(context) {
        LocalRecipesRepository(context)
    }

    val favoritesPreferences = remember(context) {
        FavoritesPreferences(context)
    }

    // Рецепты из локальной базы.
    val recipes by remember(repository) {
        repository.observeRecipes()
    }.collectAsState(initial = emptyList())

    // Сохранённые идентификаторы избранного.
    val favoriteIds by remember(favoritesPreferences) {
        favoritesPreferences.observeFavoriteIds()
    }.collectAsState(
        initial = favoritesPreferences.getFavoriteIds()
    )

    val favoriteRecipes = recipes.filter { recipe ->
        recipe.id in favoriteIds
    }

    if (favoriteRecipes.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Пока нет избранных рецептов",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Откройте рецепт и нажмите " +
                        "на сердечко, чтобы добавить его в избранное.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

    } else {

        RecipeList(
            recipes = favoriteRecipes,

            onRecipeClick = { recipeId ->
                navController.navigate(
                    "recipe/$recipeId"
                )
            }
        )
    }
}
