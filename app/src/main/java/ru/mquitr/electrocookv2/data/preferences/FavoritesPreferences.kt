package ru.mquitr.electrocookv2.data.preferences

import android.content.Context

class FavoritesPreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences(
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

    fun isFavorite(recipeId: Int): Boolean {

        return getFavoriteIds().contains(recipeId)
    }

    fun addFavorite(recipeId: Int) {

        val favorites =
            getFavoriteIds().toMutableSet()

        favorites.add(recipeId)

        saveFavorites(favorites)
    }

    fun removeFavorite(recipeId: Int) {

        val favorites =
            getFavoriteIds().toMutableSet()

        favorites.remove(recipeId)

        saveFavorites(favorites)
    }

    fun toggleFavorite(recipeId: Int) {

        if (isFavorite(recipeId)) {
            removeFavorite(recipeId)
        } else {
            addFavorite(recipeId)
        }
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