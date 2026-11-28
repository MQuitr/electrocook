
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

    private val mutex = Mutex()

    private val packageIdPattern =
        Regex("[a-z0-9_-]+")

    private val stagingPattern = Regex(
        """\.[a-z0-9_-]+-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"""
    )

    private val versionPattern =
        Regex("v[1-9][0-9]*")

    private val temporaryArchivePattern =
        Regex("""electrocook-[A-Za-z0-9_-]+\.zip""")

    suspend fun install(
        context: Context,
        baseUrl: String,
        recipePackage: RecipePackage
    ) = mutex.withLock {

        PackageInstaller.install(
            context = context,
            baseUrl = baseUrl,
            recipePackage = recipePackage
        )
    }

    suspend fun update(
        context: Context,
        baseUrl: String,
        recipePackage: RecipePackage
    ) = mutex.withLock {

        PackageInstaller.update(
            context = context,
            baseUrl = baseUrl,
            recipePackage = recipePackage
        )
    }

    suspend fun uninstall(
        context: Context,
        packageId: String
    ) = mutex.withLock {

        PackageInstaller.uninstall(
            context = context,
            packageId = packageId
        )
    }

    suspend fun recover(
        context: Context
    ): PackageRecoveryReport = mutex.withLock {

        withContext(Dispatchers.IO) {

            val appContext = context.applicationContext

            val dao = ElectroCookDatabase
                .getInstance(appContext)
                .recipesDao()

            val installedPackages = dao
                .observeInstalledPackages()
                .first()

            val installedVersions =
                installedPackages.associate {
                    it.id to it.version
                }

            val installedIds =
                installedVersions.keys

            val packagesDirectory = File(
                appContext.filesDir,
                "recipe_packages"
            )

            var removedDirectories = 0
            var removedArchives = 0

            val directories = packagesDirectory
                .listFiles()
                ?: emptyArray()

            for (directory in directories) {

                if (!directory.isDirectory) {
                    continue
                }

                val name = directory.name

                val isStaging =
                    stagingPattern.matches(name)

                val isOrphan =
                    packageIdPattern.matches(name) &&
                            name !in installedIds

                if (isStaging || isOrphan) {

                    if (!directory.deleteRecursively()) {
                        throw IOException(
                            "Не удалось очистить папку: $name"
                        )
                    }

                    removedDirectories++
                    continue
                }

                if (name !in installedIds) {
                    continue
                }

                val activeVersion =
                    installedVersions.getValue(name)

                val versionsDirectory = File(
                    directory,
                    "versions"
                )

                // В каталоге версий оставляем
                // только ту, что записана в Room.
                versionsDirectory.listFiles()
                    ?.filter { versionDirectory ->

                        versionDirectory.isDirectory &&
                                versionPattern.matches(
                                    versionDirectory.name
                                ) &&
                                versionDirectory.name !=
                                "v$activeVersion"
                    }
                    ?.forEach { obsolete ->

                        if (!obsolete.deleteRecursively()) {
                            throw IOException(
                                "Не удалось очистить версию: " +
                                        obsolete.name
                            )
                        }

                        removedDirectories++
                    }

                val activeDirectory = File(
                    versionsDirectory,
                    "v$activeVersion"
                )

                val legacyImages = File(
                    directory,
                    "images"
                )

                // Старую папку images можно удалять,
                // только когда активная версия уже
                // располагается в отдельном каталоге.
                if (
                    activeDirectory.isDirectory &&
                    legacyImages.exists()
                ) {

                    if (!legacyImages.deleteRecursively()) {
                        throw IOException(
                            "Не удалось очистить старые изображения"
                        )
                    }

                    removedDirectories++
                }
            }

            // Остатки временных ZIP.
            val cachedFiles = appContext.cacheDir
                .listFiles()
                ?: emptyArray()

            cachedFiles.forEach { file ->

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

            // Проверяем сохранённое избранное.
            val availableRecipeIds = dao
                .observeRecipes()
                .first()
                .map { it.id }
                .toSet()

            val favorites =
                FavoritesPreferences(appContext)

            val obsoleteFavoriteIds =
                favorites.getFavoriteIds() -
                        availableRecipeIds

            favorites.removeFavorites(
                obsoleteFavoriteIds
            )

            // Проверяем наличие файлов
            // активной версии каждого пакета.
            val missingPackageIds =
                installedPackages.filter { installed ->

                    val root = File(
                        packagesDirectory,
                        installed.id
                    )

                    val activeDirectory = File(
                        root,
                        "versions/v${installed.version}"
                    )

                    val legacyImages = File(
                        root,
                        "images"
                    )

                    val hasActiveFiles =
                        activeDirectory.isDirectory ||
                                (
                                        installed.version == 1 &&
                                                legacyImages.isDirectory
                                        )

                    !root.isDirectory || !hasActiveFiles
                }.map {
                    it.id
                }.sorted()

            PackageRecoveryReport(
                removedDirectories = removedDirectories,
                removedArchives = removedArchives,
                removedFavoriteIds = obsoleteFavoriteIds.size,
                missingPackageIds = missingPackageIds
            )
        }
    }
}
