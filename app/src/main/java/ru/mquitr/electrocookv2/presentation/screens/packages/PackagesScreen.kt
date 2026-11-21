
package ru.mquitr.electrocookv2.presentation.screens.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.coroutines.cancellation.CancellationException
import ru.mquitr.electrocookv2.data.mock.FakePackages
import ru.mquitr.electrocookv2.data.network.PackagesApiClient
import ru.mquitr.electrocookv2.data.preferences.PackagesPreferences
import ru.mquitr.electrocookv2.data.preferences.ServerPreferences
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import ru.mquitr.electrocookv2.presentation.components.PackageCard

@Composable
fun PackagesScreen() {

    val context = LocalContext.current

    val packagesPreferences = remember(context) {
        PackagesPreferences(context)
    }

    val serverPreferences = remember(context) {
        ServerPreferences(context)
    }

    val serverUrl = serverPreferences.getServerUrl()

    // Пока сохраняем прежние демонстрационные
    // состояния установки пакетов.
    val defaultInstalledIds = remember {
        FakePackages.packages
            .filter { it.isInstalled }
            .map { it.id }
            .toSet()
    }

    var installedPackageIds by remember(packagesPreferences) {
        mutableStateOf(
            packagesPreferences.getInstalledPackageIds(
                defaultInstalledIds
            )
        )
    }

    var packages by remember {
        mutableStateOf<List<RecipePackage>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var retryCount by remember {
        mutableIntStateOf(0)
    }

    var packageToRemove by remember {
        mutableStateOf<RecipePackage?>(null)
    }

    // Загрузка настоящего каталога с сервера.
    LaunchedEffect(serverUrl, retryCount) {

        isLoading = true
        errorMessage = null

        try {

            packages = PackagesApiClient.getPackages(
                serverUrl
            )

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: Exception) {

            packages = emptyList()

            errorMessage =
                exception.localizedMessage
                    ?: "Неизвестная ошибка соединения"

        } finally {
            isLoading = false
        }
    }

    when {

        // Ожидание ответа сервера
        isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Ошибка запроса
        errorMessage != null -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Не удалось загрузить пакеты",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = errorMessage ?: "",
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        retryCount++
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Повторить")
                }
            }
        }

        // Сервер вернул пустой список
        packages.isEmpty() -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("На сервере пока нет пакетов")
            }
        }

        // Отображаем реальные пакеты
        else -> {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = packages,
                    key = { it.id }
                ) { recipePackage ->

                    val isInstalled =
                        recipePackage.id in installedPackageIds

                    PackageCard(
                        recipePackage = recipePackage,
                        isInstalled = isInstalled,

                        onInstallClick = {

                            val updatedIds =
                                installedPackageIds + recipePackage.id

                            installedPackageIds = updatedIds

                            packagesPreferences.saveInstalledPackageIds(
                                updatedIds
                            )
                        },

                        onRemoveClick = {
                            packageToRemove = recipePackage
                        }
                    )
                }
            }
        }
    }

    // Сохраняем подтверждение удаления
    packageToRemove?.let { selectedPackage ->

        AlertDialog(
            onDismissRequest = {
                packageToRemove = null
            },

            title = {
                Text("Удалить пакет?")
            },

            text = {
                Text(
                    "Вы действительно хотите удалить пакет " +
                            "«${selectedPackage.title}» из установленных?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val updatedIds =
                            installedPackageIds - selectedPackage.id

                        installedPackageIds = updatedIds

                        packagesPreferences.saveInstalledPackageIds(
                            updatedIds
                        )

                        packageToRemove = null
                    }
                ) {

                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        packageToRemove = null
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}
