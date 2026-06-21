package ru.mquitr.electrocookv2.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.mquitr.electrocookv2.presentation.components.BottomNavigationBar
import ru.mquitr.electrocookv2.presentation.screens.favorites.FavoritesScreen
import ru.mquitr.electrocookv2.presentation.screens.home.HomeScreen
import ru.mquitr.electrocookv2.presentation.screens.packages.PackagesScreen
import ru.mquitr.electrocookv2.presentation.screens.search.SearchScreen
import ru.mquitr.electrocookv2.presentation.screens.settings.SettingsScreen

@Composable
fun App() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Search.route) {
                SearchScreen()
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }

            composable(Screen.Packages.route) {
                PackagesScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}