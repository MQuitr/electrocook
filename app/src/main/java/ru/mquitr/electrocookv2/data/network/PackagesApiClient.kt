
package ru.mquitr.electrocookv2.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import ru.mquitr.electrocookv2.domain.model.RecipePackage
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object PackagesApiClient {

    suspend fun getPackages(
        baseUrl: String
    ): List<RecipePackage> = withContext(Dispatchers.IO) {

        val normalizedUrl = baseUrl.trim().trimEnd('/')

        require(
            normalizedUrl.startsWith("http://") ||
                    normalizedUrl.startsWith("https://")
        ) {
            "Некорректный адрес сервера"
        }

        val url = URL("$normalizedUrl/api/packages")

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.setRequestProperty(
            "Accept",
            "application/json"
        )

        try {

            if (connection.responseCode != 200) {
                throw IOException(
                    "Сервер вернул HTTP ${connection.responseCode}"
                )
            }

            val response = connection.inputStream
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val jsonArray = JSONArray(response)

            buildList {

                for (index in 0 until jsonArray.length()) {

                    val item = jsonArray.getJSONObject(index)

                    add(
                        RecipePackage(
                            id = item.getString("id"),
                            title = item.getString("title"),
                            description = item.getString("description"),
                            recipeCount = item.getInt("recipeCount"),
                            version = item.getInt("version"),
                            sizeBytes = null
                        )
                    )
                }
            }

        } finally {
            connection.disconnect()
        }
    }
}
