package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.domain.model.Recipe

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    onRecipeClick: (Int) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(recipes) { recipe ->

            RecipeCard(
                recipe = recipe,
                onClick = {
                    onRecipeClick(recipe.id)
                }
            )

        }

    }

}