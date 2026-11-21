
package ru.mquitr.electrocookv2.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object PackageArchiveClient {

    private const val MAX_DOWNLOAD_BYTES = 40L * 1024 * 1024

    suspend fun download(
        baseUrl: String,
        packageId: String,
        destination: File
    ) = withContext(Dispatchers.IO) {

        require(packageId.matches(Regex("[a-z0-9_-]+"))) {
            "Некорректный идентификатор пакета"
        }

        val address = baseUrl.trim().trimEnd('/')

        require(
            address.startsWith("http://") ||
                    address.startsWith("https://")
        ) {
            "Некорректный адрес сервера"
        }

        val connection = URL(
            "$address/api/packages/$packageId/download"
        ).openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 30000
        connection.instanceFollowRedirects = false

        try {
            if (connection.responseCode != 200) {
                throw IOException(
                    "Ошибка скачивания: HTTP ${connection.responseCode}"
                )
            }

            val declaredSize = connection.contentLengthLong

            if (declaredSize > MAX_DOWNLOAD_BYTES) {
                throw IOException("Архив слишком большой")
            }

            connection.inputStream.use { input ->
                destination.outputStream().buffered().use { output ->

                    val buffer = ByteArray(8192)
                    var downloaded = 0L

                    while (true) {
                        currentCoroutineContext().ensureActive()

                        val count = input.read(buffer)

                        if (count == -1) break

                        downloaded += count

                        if (downloaded > MAX_DOWNLOAD_BYTES) {
                            throw IOException("Превышен размер архива")
                        }

                        output.write(buffer, 0, count)
                    }
                }
            }

            if (destination.length() == 0L) {
                throw IOException("Сервер вернул пустой архив")
            }

        } finally {
            connection.disconnect()
        }
    }
}
