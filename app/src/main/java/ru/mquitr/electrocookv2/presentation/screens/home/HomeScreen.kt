package ru.mquitr.electrocookv2.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import ru.mquitr.electrocookv2.presentation.components.RecipeCard
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navController: NavHostController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Популярные рецепты",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {

            items(FakeRecipes.recipes) { recipe ->

                RecipeCard(
                    recipe = recipe,

                    onClick = {

                        navController.navigate(
                            "recipe/${recipe.id}"
                        )
                    }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Для пополнения списка рецептов, скачайте дополнительный пакет в верхнем меню",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}