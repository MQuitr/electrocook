
package ru.mquitr.electrocookv2.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ru.mquitr.electrocookv2.data.local.ElectroCookDatabase
import ru.mquitr.electrocookv2.data.network.ServerHealthClient
import ru.mquitr.electrocookv2.data.preferences.ServerPreferences

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onManagePackagesClick: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember(context) {
        ElectroCookDatabase.getInstance(context)
    }

    val dao = remember(database) {
        database.recipesDao()
    }

    val installedPackages by remember(dao) {
        dao.observeInstalledPackages()
    }.collectAsState(initial = emptyList())

    val serverPreferences = remember(context) {
        ServerPreferences(context)
    }

    var serverUrl by remember(serverPreferences) {
        mutableStateOf(serverPreferences.getServerUrl())
    }

    var connectionMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isChecking by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

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

        Text(
            text = "Пакеты рецептов",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Установлено пакетов: ${installedPackages.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedButton(
            onClick = onManagePackagesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Управление пакетами")
        }

        HorizontalDivider()

        Text(
            text = "Подключение к серверу",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Адрес компьютера с ElectroCookServer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = serverUrl,
            onValueChange = {
                serverUrl = it
                connectionMessage = null
            },
            label = {
                Text("Адрес сервера")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val address = serverUrl.trim().trimEnd('/')

                if (
                    !address.startsWith("http://") &&
                    !address.startsWith("https://")
                ) {

                    connectionMessage =
                        "Укажите адрес с http:// или https://"

                } else {

                    serverPreferences.saveServerUrl(address)

                    scope.launch {

                        isChecking = true

                        try {

                            val serverInfo =
                                ServerHealthClient.checkConnection(
                                    address
                                )

                            connectionMessage =
                                "Подключение успешно: $serverInfo"

                        } catch (
                            exception: CancellationException
                        ) {
                            throw exception

                        } catch (exception: Exception) {

                            connectionMessage =
                                "Адрес сохранён. Нет соединения: " +
                                        (exception.localizedMessage
                                            ?: "неизвестная ошибка")

                        } finally {
                            isChecking = false
                        }
                    }
                }
            },
            enabled = !isChecking,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить и проверить")
        }

        if (isChecking) {
            CircularProgressIndicator()
        }

        connectionMessage?.let { message ->

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Для эмулятора: http://10.0.2.2:8080. " +
                    "Для физического телефона укажите " +
                    "локальный IP-адрес компьютера.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
