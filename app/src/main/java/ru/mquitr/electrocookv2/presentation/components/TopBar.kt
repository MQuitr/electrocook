package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import ru.mquitr.electrocookv2.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentRoute: String,
    onPackagesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    TopAppBar(

        title = {

            when (currentRoute) {

                Screen.Home.route -> {
                    Text("ElectroCook")
                }

                Screen.Search.route -> {
                    Text("Поиск")
                }

                Screen.Favorites.route -> {
                    Text("Избранное")
                }

                Screen.Packages.route -> {
                    Text("Пакеты рецептов")
                }

                Screen.Settings.route -> {
                    Text("Настройки")
                }

                Screen.About.route -> {
                    Text("О приложении")
                }

                else -> {
                    Text("ElectroCook")
                }
            }
        },

        actions = {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Меню"
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