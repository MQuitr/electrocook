package ru.mquitr.electrocookv2.data.preferences

import android.content.Context

class ThemePreferences(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "theme_settings",
            Context.MODE_PRIVATE
        )

    fun saveDarkTheme(
        enabled: Boolean
    ) {
        prefs.edit()
            .putBoolean("dark_theme", enabled)
            .apply()
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean(
            "dark_theme",
            true
        )
    }
}