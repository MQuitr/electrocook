package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onPackagesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            Text("ElectroCook")
        },

        actions = {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Пакеты рецептов")
                    },

                    onClick = {
                        expanded = false
                        onPackagesClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Настройки")
                    },

                    onClick = {
                        expanded = false
                        onSettingsClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("О приложении")
                    },

                    onClick = {
                        expanded = false
                        onAboutClick()
                    }
                )
            }
        }
    )
}