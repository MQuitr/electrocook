
package ru.mquitr.electrocookv2.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ServerHealthClient {

    suspend fun checkConnection(
        baseUrl: String
    ): String = withContext(Dispatchers.IO) {

        val normalizedUrl = baseUrl.trim().trimEnd('/')

        require(
            normalizedUrl.startsWith("http://") ||
                    normalizedUrl.startsWith("https://")
        ) {
            "Адрес должен начинаться с http:// или https://"
        }

        val url = URL("$normalizedUrl/api/health")

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

            val json = JSONObject(response)

            if (json.optString("status") != "ok") {
                throw IOException(
                    "Сервер вернул неожиданный статус"
                )
            }

            val service = json.optString(
                "service",
                "ElectroCookServer"
            )

            val apiVersion = json.optString(
                "apiVersion",
                "v1"
            )

            "$service, API $apiVersion"

        } finally {
            connection.disconnect()
        }
    }
}
