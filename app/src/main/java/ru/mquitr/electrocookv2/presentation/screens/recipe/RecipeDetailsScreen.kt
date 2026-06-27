package ru.mquitr.electrocookv2.presentation.screens.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.domain.model.Recipe

@Composable
fun RecipeDetailsScreen(
    recipe: Recipe
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineMedium
        )

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