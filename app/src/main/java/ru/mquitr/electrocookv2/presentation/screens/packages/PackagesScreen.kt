
package ru.mquitr.electrocookv2.presentation.screens.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import ru.mquitr.electrocookv2.data.network.PackagesApiClient
import ru.mquitr.electrocookv2.data.packages.PackageOperations
import ru.mquitr.electrocookv2.data.preferences.ServerPreferences
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import ru.mquitr.electrocookv2.presentation.components.PackageCard

@Composable
fun PackagesScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember(context) {
        ElectroCookDatabase.getInstance(context)
    }

    val dao = remember(database) {
        database.recipesDao()
    }

    val serverPreferences = remember(context) {
        ServerPreferences(context)
    }

    val serverUrl = serverPreferences.getServerUrl()

    val installedPackages by remember(dao) {
        dao.observeInstalledPackages()
    }.collectAsState(initial = emptyList())

    val installedRecipes by remember(dao) {
        dao.observeRecipes()
    }.collectAsState(initial = emptyList())

    var remotePackages by remember {
        mutableStateOf<List<RecipePackage>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isRecovering by remember {
        mutableStateOf(true)
    }

    var catalogError by remember {
        mutableStateOf<String?>(null)
    }

    var retryCount by remember {
        mutableIntStateOf(0)
    }

    var busyPackageId by remember {
        mutableStateOf<String?>(null)
    }

    var operationMessage by remember {
        mutableStateOf<String?>(null)
    }

    var operationSucceeded by remember {
        mutableStateOf(true)
    }

    var packageToRemove by remember {
        mutableStateOf<RecipePackage?>(null)
    }

    // Единый запуск установки, обновления
    // и удаления пакета.
    fun runPackageOperation(
        recipePackage: RecipePackage,
        successText: String,
        errorAction: String,
        operation: suspend () -> Unit
    ) {

        if (busyPackageId != null || isRecovering) {
            return
        }

        busyPackageId = recipePackage.id
        operationMessage = null

        scope.launch {

            try {

                operation()

                operationSucceeded = true
                operationMessage =
                    "Пакет «${recipePackage.title}» $successText"

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {

                operationSucceeded = false
                operationMessage =
                    "Ошибка $errorAction: " +
                            (exception.localizedMessage
                                ?: "неизвестная ошибка")

            } finally {
                busyPackageId = null
            }
        }
    }

    // Проверяем и восстанавливаем локальные файлы.
    LaunchedEffect(context) {

        isRecovering = true

        try {

            val report = PackageOperations.recover(
                context
            )

            if (report.missingPackageIds.isNotEmpty()) {

                operationSucceeded = false
                operationMessage =
                    "Отсутствуют файлы пакетов: " +
                            report.missingPackageIds.joinToString() +
                            ". Удалите и установите " +
                            "повреждённые пакеты повторно."

            } else if (
                report.removedDirectories > 0 ||
                report.removedArchives > 0
            ) {

                operationSucceeded = true
                operationMessage =
                    "Очищены файлы незавершённых операций."
            }

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: Exception) {

            operationSucceeded = false
            operationMessage =
                "Ошибка проверки хранилища: " +
                        (exception.localizedMessage
                            ?: "неизвестная ошибка")

        } finally {
            isRecovering = false
        }
    }

    // Серверный каталог.
    LaunchedEffect(serverUrl, retryCount) {

        isLoading = true
        catalogError = null

        try {

            remotePackages = PackagesApiClient.getPackages(
                serverUrl
            )

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: Exception) {

            remotePackages = emptyList()
            catalogError =
                exception.localizedMessage
                    ?: "Неизвестная ошибка соединения"

        } finally {
            isLoading = false
        }
    }

    // Локальные сведения для офлайн-режима.
    val localPackages = installedPackages.map {
            installed ->

        RecipePackage(
            id = installed.id,
            title = installed.title,
            description = installed.description,
            version = installed.version,
            recipeCount = installedRecipes.count {
                it.packageId == installed.id
            }
        )
    }

    val displayedPackages =
        remotePackages + localPackages.filter { local ->
            remotePackages.none { remote ->
                remote.id == local.id
            }
        }

    // Для каждой записи знаем именно установленную версию.
    val installedVersions = installedPackages
        .associate { it.id to it.version }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (busyPackageId != null || isRecovering) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        operationMessage?.let { message ->

            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (operationSucceeded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        if (catalogError != null) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Не удалось связаться с сервером",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = catalogError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (installedPackages.isNotEmpty()) {

                    Text(
                        text = "Установленные пакеты " +
                                "доступны локально.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = { retryCount++ },
                    enabled = busyPackageId == null &&
                            !isRecovering
                ) {
                    Text("Повторить")
                }
            }
        }

        when {

            isLoading && displayedPackages.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            displayedPackages.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        if (catalogError != null) {
                            "Нет доступных локальных пакетов"
                        } else {
                            "На сервере пока нет пакетов"
                        }
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = displayedPackages,
                        key = { it.id }
                    ) { recipePackage ->

                        val installedVersion =
                            installedVersions[recipePackage.id]

                        PackageCard(
                            recipePackage = recipePackage,
                            installedVersion = installedVersion,
                            enabled = busyPackageId == null &&
                                    !isRecovering,

                            onInstallClick = {

                                runPackageOperation(
                                    recipePackage = recipePackage,
                                    successText = "установлен",
                                    errorAction = "установки"
                                ) {

                                    PackageOperations.install(
                                        context = context,
                                        baseUrl = serverUrl,
                                        recipePackage = recipePackage
                                    )
                                }
                            },

                            onUpdateClick = {

                                if (
                                    installedVersion != null &&
                                    recipePackage.version >
                                    installedVersion
                                ) {

                                    runPackageOperation(
                                        recipePackage = recipePackage,
                                        successText = "обновлён",
                                        errorAction = "обновления"
                                    ) {

                                        PackageOperations.update(
                                            context = context,
                                            baseUrl = serverUrl,
                                            recipePackage = recipePackage
                                        )
                                    }
                                }
                            },

                            onRemoveClick = {

                                if (
                                    busyPackageId == null &&
                                    !isRecovering
                                ) {
                                    packageToRemove = recipePackage
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Подтверждение удаления.
    packageToRemove?.let { selectedPackage ->

        AlertDialog(
            onDismissRequest = {
                if (busyPackageId == null) {
                    packageToRemove = null
                }
            },

            title = {
                Text("Удалить пакет?")
            },

            text = {
                Text(
                    "Удалить пакет «${selectedPackage.title}» " +
                            "вместе с его рецептами и фотографиями?"
                )
            },

            confirmButton = {

                TextButton(
                    enabled = busyPackageId == null &&
                            !isRecovering,

                    onClick = {

                        packageToRemove = null

                        runPackageOperation(
                            recipePackage = selectedPackage,
                            successText = "удалён",
                            errorAction = "удаления"
                        ) {

                            PackageOperations.uninstall(
                                context = context,
                                packageId = selectedPackage.id
                            )
                        }
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
                    enabled = busyPackageId == null &&
                            !isRecovering,
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
