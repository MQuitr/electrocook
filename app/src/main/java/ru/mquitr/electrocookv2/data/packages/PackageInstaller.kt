
package ru.mquitr.electrocookv2.data.packages

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.mquitr.electrocookv2.data.local.AllergenEntity
import ru.mquitr.electrocookv2.data.local.CookingStepEntity
import ru.mquitr.electrocookv2.data.local.ElectroCookDatabase
import ru.mquitr.electrocookv2.data.local.IngredientEntity
import ru.mquitr.electrocookv2.data.local.InstalledPackageEntity
import ru.mquitr.electrocookv2.data.local.RecipeEntity
import ru.mquitr.electrocookv2.data.network.PackageArchiveClient
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.zip.ZipInputStream

object PackageInstaller {

    private val packageIdPattern = Regex("[a-z0-9_-]+")

    private val imagePathPattern =
        Regex("images/[A-Za-z0-9_-]+\\.(webp|png|jpg|jpeg)")

    private const val MAX_ENTRIES = 200
    private const val MAX_JSON_BYTES = 2L * 1024 * 1024
    private const val MAX_IMAGE_BYTES = 8L * 1024 * 1024
    private const val MAX_TOTAL_BYTES = 60L * 1024 * 1024

    private data class ParsedPackage(
        val info: InstalledPackageEntity,
        val recipes: List<RecipeEntity>,
        val ingredients: List<IngredientEntity>,
        val steps: List<CookingStepEntity>,
        val allergens: List<AllergenEntity>,
        val imagePaths: Set<String>
    )

    suspend fun install(
        context: Context,
        baseUrl: String,
        recipePackage: RecipePackage
    ) = withContext(Dispatchers.IO) {

        val packageId = recipePackage.id

        require(packageIdPattern.matches(packageId)) {
            "Некорректный идентификатор пакета"
        }

        val appContext = context.applicationContext
        val dao = ElectroCookDatabase
            .getInstance(appContext)
            .recipesDao()

        val alreadyInstalled = dao.observeInstalledPackages()
            .first()
            .any { it.id == packageId }

        check(!alreadyInstalled) {
            "Пакет уже установлен"
        }

        val packagesDir = File(
            appContext.filesDir,
            "recipe_packages"
        )

        if (!packagesDir.isDirectory && !packagesDir.mkdirs()) {
            throw IOException("Не удалось создать папку пакетов")
        }

        val archiveFile = File.createTempFile(
            "electrocook-",
            ".zip",
            appContext.cacheDir
        )

        val stagingDir = File(
            packagesDir,
            ".$packageId-${UUID.randomUUID()}"
        )

        val installedDir = File(packagesDir, packageId)

        var filesMoved = false
        var databaseSaved = false

        try {
            PackageArchiveClient.download(
                baseUrl = baseUrl,
                packageId = packageId,
                destination = archiveFile
            )

            if (!stagingDir.mkdirs()) {
                throw IOException(
                    "Не удалось создать временную папку"
                )
            }

            val manifest = extractArchive(
                archiveFile,
                stagingDir
            )

            val parsed = parseManifest(
                manifest,
                recipePackage
            )

            // Проверяем, что все фотографии из JSON
            // действительно находятся в архиве.
            parsed.imagePaths.forEach { path ->
                val image = File(stagingDir, path)

                if (!image.isFile || image.length() == 0L) {
                    throw IOException(
                        "Отсутствует изображение: $path"
                    )
                }
            }

            // Остатки старой незавершённой установки.
            if (installedDir.exists() &&
                !installedDir.deleteRecursively()
            ) {
                throw IOException(
                    "Не удалось очистить старые файлы"
                )
            }

            currentCoroutineContext().ensureActive()

            if (!stagingDir.renameTo(installedDir)) {
                throw IOException(
                    "Не удалось сохранить изображения"
                )
            }

            filesMoved = true

            dao.replacePackageData(
                recipePackage = parsed.info,
                recipes = parsed.recipes,
                ingredients = parsed.ingredients,
                steps = parsed.steps,
                allergens = parsed.allergens
            )

            databaseSaved = true

        } finally {
            archiveFile.delete()
            stagingDir.deleteRecursively()

            // Если Room не сохранил пакет,
            // не оставляем фотографии установленными.
            if (filesMoved && !databaseSaved) {
                installedDir.deleteRecursively()
            }
        }
    }

    suspend fun uninstall(
        context: Context,
        packageId: String
    ) = withContext(Dispatchers.IO) {

        require(packageIdPattern.matches(packageId)) {
            "Некорректный идентификатор пакета"
        }

        val appContext = context.applicationContext

        val dao = ElectroCookDatabase
            .getInstance(appContext)
            .recipesDao()

        // Удаление пакета каскадно удаляет
        // его рецепты, ингредиенты, шаги и аллергены.
        dao.deletePackage(packageId)

        val packageDir = File(
            File(appContext.filesDir, "recipe_packages"),
            packageId
        )

        if (packageDir.exists() &&
            !packageDir.deleteRecursively()
        ) {
            throw IOException(
                "Пакет удалён из базы, но файлы удалить не удалось"
            )
        }
    }

    private fun extractArchive(
        archiveFile: File,
        stagingDir: File
    ): String {

        val seenEntries = mutableSetOf<String>()
        var manifest: String? = null
        var totalBytes = 0L

        ZipInputStream(
            archiveFile.inputStream().buffered()
        ).use { zip ->

            while (true) {
                val entry = zip.nextEntry ?: break
                val name = entry.name

                if (entry.isDirectory ||
                    !seenEntries.add(name) ||
                    seenEntries.size > MAX_ENTRIES
                ) {
                    throw IOException(
                        "Некорректная структура ZIP"
                    )
                }

                if (name == "package.json") {

                    val output = ByteArrayOutputStream()

                    totalBytes += copyLimited(
                        zip,
                        output,
                        MAX_JSON_BYTES
                    )

                    manifest = output.toString("UTF-8")

                } else {

                    if (!imagePathPattern.matches(name)) {
                        throw IOException(
                            "Недопустимый путь в ZIP: $name"
                        )
                    }

                    val destination = File(stagingDir, name)

                    if (!destination.parentFile!!.isDirectory &&
                        !destination.parentFile!!.mkdirs()
                    ) {
                        throw IOException(
                            "Не удалось создать папку изображений"
                        )
                    }

                    destination.outputStream().use { output ->
                        totalBytes += copyLimited(
                            zip,
                            output,
                            MAX_IMAGE_BYTES
                        )
                    }
                }

                if (totalBytes > MAX_TOTAL_BYTES) {
                    throw IOException(
                        "Превышен допустимый размер пакета"
                    )
                }

                zip.closeEntry()
            }
        }

        return manifest
            ?: throw IOException(
                "В ZIP отсутствует package.json"
            )
    }

    private fun copyLimited(
        input: InputStream,
        output: OutputStream,
        limit: Long
    ): Long {

        val buffer = ByteArray(8192)
        var copied = 0L

        while (true) {
            val count = input.read(buffer)
            if (count == -1) break

            copied += count

            if (copied > limit) {
                throw IOException(
                    "Слишком большой файл внутри ZIP"
                )
            }

            output.write(buffer, 0, count)
        }

        return copied
    }

    private fun parseManifest(
        json: String,
        expected: RecipePackage
    ): ParsedPackage {

        val root = JSONObject(json)

        require(root.getInt("formatVersion") == 1) {
            "Неподдерживаемый формат пакета"
        }

        require(root.getString("id") == expected.id) {
            "Идентификатор пакета не совпадает"
        }

        require(root.getInt("version") == expected.version) {
            "Версия пакета изменилась. Обновите каталог"
        }

        val recipeArray = root.getJSONArray("recipes")

        require(recipeArray.length() == expected.recipeCount) {
            "Количество рецептов не совпадает с каталогом"
        }

        val recipes = mutableListOf<RecipeEntity>()
        val ingredients = mutableListOf<IngredientEntity>()
        val steps = mutableListOf<CookingStepEntity>()
        val allergens = mutableListOf<AllergenEntity>()
        val imagePaths = mutableSetOf<String>()
        val recipeIds = mutableSetOf<Int>()

        fun readImagePath(obj: JSONObject): String? {

            if (!obj.has("imagePath") ||
                obj.isNull("imagePath")
            ) {
                return null
            }

            val path = obj.getString("imagePath")

            require(imagePathPattern.matches(path)) {
                "Недопустимый путь изображения: $path"
            }

            imagePaths.add(path)
            return path
        }

        for (i in 0 until recipeArray.length()) {

            val recipe = recipeArray.getJSONObject(i)
            val recipeId = recipe.getInt("id")

            require(recipeId > 0 && recipeIds.add(recipeId)) {
                "Повторяющийся или неверный ID рецепта"
            }

            require(
                recipe.getString("packageId") == expected.id
            ) {
                "Рецепт относится к другому пакету"
            }

            recipes.add(
                RecipeEntity(
                    id = recipeId,
                    packageId = expected.id,
                    title = recipe.getString("title"),
                    description = recipe.getString("description"),
                    calories = recipe.getInt("calories"),
                    proteins = recipe.getDouble("proteins"),
                    fats = recipe.getDouble("fats"),
                    carbs = recipe.getDouble("carbs"),
                    cookingTime = recipe.getInt("cookingTime"),
                    servings = recipe.getInt("servings"),
                    imagePath = readImagePath(recipe)
                )
            )

            val ingredientArray =
                recipe.getJSONArray("ingredients")

            for (j in 0 until ingredientArray.length()) {

                val ingredient =
                    ingredientArray.getJSONObject(j)

                ingredients.add(
                    IngredientEntity(
                        recipeId = recipeId,
                        position = j + 1,
                        name = ingredient.getString("name"),
                        amount = ingredient.getString("amount")
                    )
                )
            }

            val stepArray = recipe.getJSONArray("steps")
            val stepNumbers = mutableSetOf<Int>()

            for (j in 0 until stepArray.length()) {

                val step = stepArray.getJSONObject(j)
                val stepNumber = step.getInt("stepNumber")

                require(
                    stepNumber > 0 &&
                            stepNumbers.add(stepNumber)
                ) {
                    "Повторяющийся номер шага"
                }

                steps.add(
                    CookingStepEntity(
                        recipeId = recipeId,
                        stepNumber = stepNumber,
                        description = step.getString(
                            "description"
                        ),
                        imagePath = readImagePath(step)
                    )
                )
            }

            val allergenArray =
                recipe.getJSONArray("allergens")

            val uniqueAllergens = mutableSetOf<String>()

            for (j in 0 until allergenArray.length()) {

                val allergen = allergenArray.getString(j)

                require(uniqueAllergens.add(allergen)) {
                    "Повторяющийся аллерген"
                }

                allergens.add(
                    AllergenEntity(
                        recipeId = recipeId,
                        allergen = allergen
                    )
                )
            }
        }

        return ParsedPackage(
            info = InstalledPackageEntity(
                id = expected.id,
                title = root.getString("title"),
                description = root.getString("description"),
                version = expected.version
            ),
            recipes = recipes,
            ingredients = ingredients,
            steps = steps,
            allergens = allergens,
            imagePaths = imagePaths
        )
    }
}
