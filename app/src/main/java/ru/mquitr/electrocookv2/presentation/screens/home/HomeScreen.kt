
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
    navController: NavHostController
) {
    val context = LocalContext.current

    val repository = remember(context) {
        LocalRecipesRepository(context)
    }

    val recipes by remember(repository) {
        repository.observeRecipes()
    }.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Установленные рецепты",
            style = MaterialTheme.typography.titleLarge
        )

        if (recipes.isEmpty()) {

            Text(
                text = "Пока нет установленных рецептов",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "Откройте раздел «Пакеты рецептов» " +
                        "и установите любой пакет.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

        } else {

            RecipeList(
                recipes = recipes,
                onRecipeClick = { recipeId ->
                    navController.navigate(
                        "recipe/$recipeId"
                    )
                }
            )
        }
    }
}
