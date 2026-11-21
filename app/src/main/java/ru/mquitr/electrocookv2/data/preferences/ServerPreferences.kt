
package ru.mquitr.electrocookv2.data.preferences

import android.content.Context

class ServerPreferences(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "server_preferences",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val SERVER_URL_KEY = "server_url"

        const val DEFAULT_SERVER_URL =
            "http://10.0.2.2:8080"
    }

    fun getServerUrl(): String {
        return preferences.getString(
            SERVER_URL_KEY,
            DEFAULT_SERVER_URL
        ) ?: DEFAULT_SERVER_URL
    }

    fun saveServerUrl(url: String) {
        preferences.edit()
            .putString(
                SERVER_URL_KEY,
                url.trim().trimEnd('/')
            )
            .apply()
    }
}
