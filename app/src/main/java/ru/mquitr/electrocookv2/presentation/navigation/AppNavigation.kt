package ru.mquitr.electrocookv2.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import ru.mquitr.electrocookv2.presentation.components.BottomNavigationBar
import ru.mquitr.electrocookv2.presentation.components.TopBar
import ru.mquitr.electrocookv2.presentation.screens.about.AboutScreen
import ru.mquitr.electrocookv2.presentation.screens.favorites.FavoritesScreen
import ru.mquitr.electrocookv2.presentation.screens.home.HomeScreen
import ru.mquitr.electrocookv2.presentation.screens.packages.PackagesScreen
import ru.mquitr.electrocookv2.presentation.screens.recipe.RecipeDetailsScreen
import ru.mquitr.electrocookv2.presentation.screens.search.SearchScreen
import ru.mquitr.electrocookv2.presentation.screens.settings.SettingsScreen

@Composable
fun App(
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {

    val navController = rememberNavController()

    Scaffold(

        topBar = {

            TopBar(

                onPackagesClick = {
                    navController.navigate(Screen.Packages.route)
                },

                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },

                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        },

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
                HomeScreen(
                    navController = navController
                )
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
                SettingsScreen(
                    darkTheme = darkTheme,
                    onThemeChanged = onThemeChanged
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }

            composable(
                route = "recipe/{recipeId}",
                arguments = listOf(
                    navArgument("recipeId") {
                        type = NavType.IntType
                    }
                )
            ) {

                val recipeId =
                    it.arguments?.getInt("recipeId") ?: return@composable

                val recipe =
                    FakeRecipes.recipes.find { recipe ->
                        recipe.id == recipeId
                    } ?: return@composable

                RecipeDetailsScreen(
                    recipe = recipe
                )
            }
        }
    }
}