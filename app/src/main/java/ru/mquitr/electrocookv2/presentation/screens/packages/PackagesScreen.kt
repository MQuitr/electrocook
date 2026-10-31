
package ru.mquitr.electrocookv2.presentation.screens.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakePackages
import ru.mquitr.electrocookv2.data.preferences.PackagesPreferences
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import ru.mquitr.electrocookv2.presentation.components.PackageCard

@Composable
fun PackagesScreen() {

    val context = LocalContext.current

    val packagesPreferences = remember(context) {
        PackagesPreferences(context)
    }

    // Пакеты, установленные по умолчанию
    val defaultInstalledIds = remember {
        FakePackages.packages
            .filter { it.isInstalled }
            .map { it.id }
            .toSet()
    }

    // Загружаем сохранённые состояния
    var installedPackageIds by remember(packagesPreferences) {
        mutableStateOf(
            packagesPreferences.getInstalledPackageIds(
                defaultInstalledIds
            )
        )
    }

    // Пакет, для которого запрошено удаление
    var packageToRemove by remember {
        mutableStateOf<RecipePackage?>(null)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            items = FakePackages.packages,
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

                    // Сначала запрашиваем подтверждение
                    packageToRemove = recipePackage
                }
            )
        }
    }

    // Диалог подтверждения удаления
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
