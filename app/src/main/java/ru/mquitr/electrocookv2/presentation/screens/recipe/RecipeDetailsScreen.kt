package ru.mquitr.electrocookv2.presentation.screens.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.preferences.FavoritesPreferences
import ru.mquitr.electrocookv2.domain.model.Recipe

@Composable
fun RecipeDetailsScreen(
    recipe: Recipe
) {

    val context = LocalContext.current

    val favoritesPreferences = remember {
        FavoritesPreferences(context)
    }

    var isFavorite by remember {
        mutableStateOf(
            favoritesPreferences.isFavorite(recipe.id)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(
                onClick = {

                    favoritesPreferences.toggleFavorite(recipe.id)

                    isFavorite = favoritesPreferences.isFavorite(recipe.id)
                }
            ) {

                Icon(
                    imageVector =
                        if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,

                    contentDescription = "Добавить в избранное"
                )
            }
        }

        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Калории: ${recipe.calories} ккал")
        Text("Белки: ${recipe.proteins} г")
        Text("Жиры: ${recipe.fats} г")
        Text("Углеводы: ${recipe.carbs} г")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Время приготовления: ${recipe.cookingTime} мин")
        Text("Количество порций: ${recipe.servings}")

        HorizontalDivider()

        Text(
            text = "Ингредиенты",
            style = MaterialTheme.typography.titleLarge
        )

        recipe.ingredients.forEach { ingredient ->

            Text(
                text = "• ${ingredient.name} — ${ingredient.amount}"
            )
        }

        HorizontalDivider()

        Text(
            text = "Приготовление",
            style = MaterialTheme.typography.titleLarge
        )

        recipe.steps.forEach { step ->

            Text(
                text = "${step.stepNumber}. ${step.description}"
            )
        }
    }
}