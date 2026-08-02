package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AllergensFilterCard(
    allergens: List<String>,
    selectedAllergens: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        allergens.forEach { allergen ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = allergen,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )

                Checkbox(
                    checked = allergen in selectedAllergens,

                    onCheckedChange = { checked ->

                        val updated =
                            selectedAllergens.toMutableSet()

                        if (checked)
                            updated.add(allergen)
                        else
                            updated.remove(allergen)

                        onSelectionChanged(updated)
                    }
                )
            }
        }
    }
}