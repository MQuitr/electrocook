package ru.mquitr.electrocookv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.mquitr.electrocookv2.presentation.navigation.App
import ru.mquitr.electrocookv2.ui.theme.ElectroCookAssistantTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ElectroCookAssistantTheme {
                App()
            }
        }
    }
}