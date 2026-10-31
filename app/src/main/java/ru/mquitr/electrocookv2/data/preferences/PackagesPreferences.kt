
package ru.mquitr.electrocookv2.data.preferences

import android.content.Context

class PackagesPreferences(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "packages_preferences",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val INSTALLED_PACKAGES_KEY =
            "installed_package_ids"
    }

    fun getInstalledPackageIds(
        defaultIds: Set<String> = emptySet()
    ): Set<String> {

        return if (preferences.contains(INSTALLED_PACKAGES_KEY)) {

            preferences.getStringSet(
                INSTALLED_PACKAGES_KEY,
                emptySet()
            )?.toSet() ?: emptySet()

        } else {
            defaultIds
        }
    }

    fun saveInstalledPackageIds(
        installedIds: Set<String>
    ) {

        preferences.edit()
            .putStringSet(
                INSTALLED_PACKAGES_KEY,
                installedIds.toSet()
            )
            .apply()
    }
}
