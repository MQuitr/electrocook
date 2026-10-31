
package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import java.util.Locale

@Composable
fun PackageCard(
    recipePackage: RecipePackage,
    isInstalled: Boolean,
    onInstallClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val recipeCount = recipePackage.recipeCount

    val recipeWord = when {
        recipeCount % 100 in 11..14 -> "рецептов"
        recipeCount % 10 == 1 -> "рецепт"
        recipeCount % 10 in 2..4 -> "рецепта"
        else -> "рецептов"
    }

    val sizeMb = recipePackage.sizeBytes / 1_000_000.0

    val formattedSize = String.format(
        Locale.getDefault(),
        "%.1f",
        sizeMb
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Название пакета
            Text(
                text = recipePackage.title,
                style = MaterialTheme.typography.titleLarge
            )

            // Описание курсивом
            Text(
                text = recipePackage.description,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Количество рецептов и размер пакета
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = recipeCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = recipeWord,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = formattedSize,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "МБ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Состояние установки
            if (isInstalled) {

                Text(
                    text = "Установлен",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                OutlinedButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить пакет")
                }

            } else {

                Button(
                    onClick = onInstallClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Установить")
                }
            }
        }
    }
}
