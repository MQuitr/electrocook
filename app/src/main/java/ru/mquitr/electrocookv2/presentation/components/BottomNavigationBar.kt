package ru.mquitr.electrocookv2.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.mquitr.electrocookv2.presentation.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {

    val screens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Favorites,
        Screen.Packages,
        Screen.Settings
    )

    val navBackStackEntry =
        navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry.value?.destination?.route

    NavigationBar {

        screens.forEach { screen ->

            NavigationBarItem(
                selected = currentRoute == screen.route,

                onClick = {

                    navController.navigate(screen.route) {

                        popUpTo(navController.graph.startDestinationId)

                        launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },

                label = {
                    Text(screen.title)
                }
            )
        }
    }
}