package ru.mquitr.electrocookv2.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {

    data object Home : Screen(
        route = "home",
        title = "Главная",
        icon = Icons.Default.Home
    )

    data object Search : Screen(
        route = "search",
        title = "Поиск",
        icon = Icons.Default.Search
    )

    data object Favorites : Screen(
        route = "favorites",
        title = "Избранное",
        icon = Icons.Default.Favorite
    )

    data object Packages : Screen(
        route = "packages",
        title = "Пакеты рецептов"
    )

    data object Settings : Screen(
        route = "settings",
        title = "Настройки"
    )

    data object About : Screen(
        route = "about",
        title = "О приложении"
    )
}