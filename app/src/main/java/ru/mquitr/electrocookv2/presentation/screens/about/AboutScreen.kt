package ru.mquitr.electrocookv2.presentation.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "ElectroCook",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Кулинарный ассистент"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Версия 2.0"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Разработка гибридного Android-приложения «Кулинарный ассистент»"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Выпускная квалификационная работа"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Автор: Максим Мусиенко"
        )
    }
}