
package ru.mquitr.electrocookv2.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class FavoritesPreferences(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "favorites_preferences",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val FAVORITES_KEY = "favorite_recipe_ids"
    }

    fun getFavoriteIds(): Set<Int> {

        return preferences
            .getStringSet(FAVORITES_KEY, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    // Автоматическое наблюдение за избранным.
    fun observeFavoriteIds(): Flow<Set<Int>> = callbackFlow {

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener {
                    _, key ->

                if (key == FAVORITES_KEY) {
                    trySend(getFavoriteIds())
                }
            }

        preferences.registerOnSharedPreferenceChangeListener(
            listener
        )

        trySend(getFavoriteIds())

        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(
                listener
            )
        }
    }.distinctUntilChanged()

    fun isFavorite(recipeId: Int): Boolean {
        return recipeId in getFavoriteIds()
    }

    fun addFavorite(recipeId: Int) {

        saveFavorites(
            getFavoriteIds() + recipeId
        )
    }

    fun removeFavorite(recipeId: Int) {

        saveFavorites(
            getFavoriteIds() - recipeId
        )
    }

    fun toggleFavorite(recipeId: Int) {

        if (isFavorite(recipeId)) {
            removeFavorite(recipeId)
        } else {
            addFavorite(recipeId)
        }
    }

    // Используется при удалении пакета рецептов.
    fun removeFavorites(recipeIds: Set<Int>) {

        if (recipeIds.isEmpty()) return

        saveFavorites(
            getFavoriteIds() - recipeIds
        )
    }

    private fun saveFavorites(favorites: Set<Int>) {

        preferences.edit()
            .putStringSet(
                FAVORITES_KEY,
                favorites.map { it.toString() }.toSet()
            )
            .apply()
    }
}
