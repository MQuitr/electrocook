package ru.mquitr.electrocookv2.presentation.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import ru.mquitr.electrocookv2.data.preferences.FavoritesPreferences
import ru.mquitr.electrocookv2.presentation.components.RecipeCard

@Composable
fun FavoritesScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val favoritesPreferences = remember {
        FavoritesPreferences(context)
    }

    val favoriteRecipes = FakeRecipes.recipes.filter {
        favoritesPreferences.isFavorite(it.id)
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
                modifier = Modifier.padding(top = 8.dp),
                text = "Добавьте понравившийся рецепт, нажав ❤️ на странице рецепта."
            )

        }

    } else {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {

            items(favoriteRecipes) { recipe ->

                RecipeCard(
                    recipe = recipe,
                    onClick = {
                        navController.navigate("recipe/${recipe.id}")
                    }
                )

            }

        }

    }
}