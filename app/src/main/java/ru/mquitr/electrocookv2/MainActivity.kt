package ru.mquitr.electrocookv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.mquitr.electrocookv2.presentation.navigation.App
import ru.mquitr.electrocookv2.ui.theme.ElectroCookAssistantTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.mquitr.electrocookv2.data.preferences.ThemePreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val preferences =
                ThemePreferences(this)

            var darkTheme by remember {
                mutableStateOf(
                    preferences.isDarkTheme()
                )
            }

            ElectroCookAssistantTheme(
                darkTheme = darkTheme
            ) {

                App(
                    darkTheme = darkTheme,

                    onThemeChanged = { enabled ->

                        darkTheme = enabled

                        preferences.saveDarkTheme(
                            enabled
                        )
                    }
                )
            }
        }
    }
}