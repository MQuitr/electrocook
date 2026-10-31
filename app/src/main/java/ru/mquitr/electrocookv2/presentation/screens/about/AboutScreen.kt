
package ru.mquitr.electrocookv2.presentation.screens.about

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {

    val context = LocalContext.current

    val versionName = remember(context) {
        try {
            val packageInfo =
                if (Build.VERSION.SDK_INT >= 33) {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.PackageInfoFlags.of(0L)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    )
                }

            packageInfo.versionName ?: "Неизвестна"

        } catch (_: PackageManager.NameNotFoundException) {
            "Неизвестна"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Логотип-заглушка
        Surface(
            modifier = Modifier.size(76.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EC",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Название и описание приложения
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "ElectroCook v2",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Кулинарный ассистент",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Версия приложения
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Версия $versionName",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        // Информация о проекте
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "О проекте",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "ElectroCook — кулинарный ассистент " +
                            "с поиском рецептов, фильтрами и избранным.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Проект предусматривает загрузку " +
                            "тематических пакетов рецептов " +
                            "и их использование без подключения " +
                            "к серверу.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Информация о дипломной работе
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Выпускная квалификационная работа",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Разработка гибридного " +
                            "Android-приложения «Кулинарный ассистент».",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                Text(
                    text = "Автор",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Максим Мусиенко",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
