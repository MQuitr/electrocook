package ru.examplemquit.electrocook.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.examplemquit.electrocook.data.RecipeDao
import ru.examplemquit.electrocook.model.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {
    val readAllData: LiveData<List<Recipe>> = recipeDao.readAllData()
    val favoriteRecipes: LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes()

    suspend fun addRecipe(recipe: Recipe){
        recipeDao.addRecipe(recipe)
    }

    fun searchRecipes(query: String): LiveData<List<Recipe>> {
        return recipeDao.searchRecipes("%$query%")
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        recipeDao.updateRecipe(recipeId, isFavorite)
    }

    suspend fun fetchRandomRecipe(): Recipe? {
        return withContext(Dispatchers.IO) {
            recipeDao.getRandomRecipe()
        }
    }
}