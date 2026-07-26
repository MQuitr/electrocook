package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NumericFilterCard(
    options: List<FilterOption>,
    selectedIndex: Int,
    customValue: String,
    maxValue: Int,
    customLabel: String,
    onSelectedChange: (Int) -> Unit,
    onCustomValueChange: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        options.forEachIndexed { index, option ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {

                RadioButton(
                    selected = selectedIndex == index,
                    onClick = {
                        onSelectedChange(index)
                    }
                )

                Text(
                    text = option.title,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        }

        if (selectedIndex == options.lastIndex) {

            OutlinedTextField(
                value = customValue,

                onValueChange = {

                    if (it.all(Char::isDigit) || it.isEmpty()) {

                        val value = it.toIntOrNull()

                        if (value == null || value <= maxValue) {
                            onCustomValueChange(it)
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),

                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                label = {
                    Text(customLabel)
                },

                supportingText = {
                    Text("От 0 до $maxValue")
                }
            )
        }
    }
}