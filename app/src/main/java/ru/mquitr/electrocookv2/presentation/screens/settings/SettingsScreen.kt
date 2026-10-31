
package ru.mquitr.electrocookv2.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakePackages
import ru.mquitr.electrocookv2.data.preferences.PackagesPreferences

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onManagePackagesClick: () -> Unit
) {

    val context = LocalContext.current

    val packagesPreferences = remember(context) {
        PackagesPreferences(context)
    }

    val defaultInstalledIds = remember {
        FakePackages.packages
            .filter { it.isInstalled }
            .map { it.id }
            .toSet()
    }

    val installedIds =
        packagesPreferences.getInstalledPackageIds(
            defaultInstalledIds
        )

    val installedCount = FakePackages.packages.count {
        it.id in installedIds
    }

    val totalCount = FakePackages.packages.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Настройки внешнего вида
        Text(
            text = "Внешний вид",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Тёмная тема",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = darkTheme,
                onCheckedChange = onThemeChanged
            )
        }

        HorizontalDivider()

        // Информация о пакетах
        Text(
            text = "Пакеты рецептов",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Установлено пакетов: " +
                    "$installedCount из $totalCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedButton(
            onClick = onManagePackagesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Управление пакетами")
        }
    }
}
