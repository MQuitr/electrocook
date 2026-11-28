
package ru.mquitr.electrocookv2.presentation.screens.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import ru.mquitr.electrocookv2.data.local.LocalRecipesRepository
import ru.mquitr.electrocookv2.domain.model.Recipe

@Composable
fun LocalRecipeRoute(
    recipeId: Int
) {
    val context = LocalContext.current

    val repository = remember(context) {
        LocalRecipesRepository(context)
    }

    var recipe by remember(recipeId) {
        mutableStateOf<Recipe?>(null)
    }

    var isLoading by remember(recipeId) {
        mutableStateOf(true)
    }

    var errorMessage by remember(recipeId) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(recipeId, repository) {

        isLoading = true
        errorMessage = null
        recipe = null

        try {
            recipe = repository.getRecipeById(recipeId)

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: Exception) {
            errorMessage =
                exception.localizedMessage
                    ?: "Неизвестная ошибка загрузки"

        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        recipe != null -> {
            RecipeDetailsScreen(
                recipe = requireNotNull(recipe)
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage
                        ?: "Рецепт не найден. " +
                        "Возможно, его пакет был удалён.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
