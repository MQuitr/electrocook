
package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mquitr.electrocookv2.domain.model.Recipe

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Фотография блюда из локального хранилища.
            recipe.imageUrl?.let { imageUri ->

                AsyncImage(
                    model = imageUri,

                    contentDescription =
                        "Фотография блюда: ${recipe.title}",

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp)),

                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "${recipe.calories} ккал",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${recipe.cookingTime} мин",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
