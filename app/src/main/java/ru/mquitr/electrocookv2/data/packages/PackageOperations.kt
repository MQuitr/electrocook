
package ru.mquitr.electrocookv2.data.packages

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.mquitr.electrocookv2.data.local.ElectroCookDatabase
import ru.mquitr.electrocookv2.data.preferences.FavoritesPreferences
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import java.io.File
import java.io.IOException

data class PackageRecoveryReport(
    val removedDirectories: Int,
    val removedArchives: Int,
    val removedFavoriteIds: Int,
    val missingPackageIds: List<String>
)

object PackageOperations {

    // Одновременно выполняется только одна
    // операция над хранилищем пакетов.
    private val mutex = Mutex()

    private val packageIdPattern =
        Regex("[a-z0-9_-]+")

    private val stagingPattern = Regex(
        """\.[a-z0-9_-]+-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"""
    )

    private val temporaryArchivePattern =
        Regex("""electrocook-[A-Za-z0-9_-]+\.zip""")

    suspend fun install(
        context: Context,
        baseUrl: String,
        recipePackage: RecipePackage
    ) {
        mutex.withLock {
            PackageInstaller.install(
                context = context,
                baseUrl = baseUrl,
                recipePackage = recipePackage
            )
        }
    }

    suspend fun uninstall(
        context: Context,
        packageId: String
    ) {
        mutex.withLock {
            PackageInstaller.uninstall(
                context = context,
                packageId = packageId
            )
        }
    }

    suspend fun recover(
        context: Context
    ): PackageRecoveryReport = mutex.withLock {

        withContext(Dispatchers.IO) {

            val appContext = context.applicationContext

            val dao = ElectroCookDatabase
                .getInstance(appContext)
                .recipesDao()

            // Room — источник достоверных сведений
            // об установленных пакетах.
            val installedIds = dao
                .observeInstalledPackages()
                .first()
                .map { it.id }
                .toSet()

            val packagesDirectory = File(
                appContext.filesDir,
                "recipe_packages"
            )

            var removedDirectories = 0
            var removedArchives = 0

            val existingDirectories =
                packagesDirectory.listFiles()
                    ?: emptyArray()

            for (directory in existingDirectories) {

                if (!directory.isDirectory) {
                    continue
                }

                val name = directory.name

                val isStagingDirectory =
                    stagingPattern.matches(name)

                val isOrphanDirectory =
                    packageIdPattern.matches(name) &&
                            name !in installedIds

                if (isStagingDirectory ||
                    isOrphanDirectory
                ) {

                    if (!directory.deleteRecursively()) {
                        throw IOException(
                            "Не удалось очистить папку: $name"
                        )
                    }

                    removedDirectories++
                }
            }

            // Удаляем временные ZIP-файлы,
            // оставшиеся после прерывания процесса.
            val cachedFiles =
                appContext.cacheDir.listFiles()
                    ?: emptyArray()

            for (file in cachedFiles) {

                if (
                    file.isFile &&
                    temporaryArchivePattern.matches(
                        file.name
                    )
                ) {

                    if (!file.delete()) {
                        throw IOException(
                            "Не удалось удалить временный ZIP"
                        )
                    }

                    removedArchives++
                }
            }

            // Удаляем из избранного ID рецептов,
            // которые уже отсутствуют в Room.
            val availableRecipeIds = dao
                .observeRecipes()
                .first()
                .map { it.id }
                .toSet()

            val favorites =
                FavoritesPreferences(appContext)

            val staleFavoriteIds =
                favorites.getFavoriteIds()
                    .filterNot {
                        it in availableRecipeIds
                    }
                    .toSet()

            favorites.removeFavorites(
                staleFavoriteIds
            )

            // Установленные пакеты не удаляем,
            // даже если обнаружены проблемы с файлами.
            val missingPackageIds = installedIds
                .filter { packageId ->

                    !packageIdPattern.matches(packageId) ||
                            !File(
                                packagesDirectory,
                                packageId
                            ).isDirectory
                }
                .sorted()

            PackageRecoveryReport(
                removedDirectories = removedDirectories,
                removedArchives = removedArchives,
                removedFavoriteIds = staleFavoriteIds.size,
                missingPackageIds = missingPackageIds
            )
        }
    }
}
