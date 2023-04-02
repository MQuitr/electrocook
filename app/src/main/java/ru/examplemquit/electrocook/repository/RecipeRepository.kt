package ru.examplemquit.electrocook.repository

import android.app.DownloadManager.Query
import androidx.lifecycle.LiveData
import ru.examplemquit.electrocook.data.RecipeDao
import ru.examplemquit.electrocook.model.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {
    val readAllData: LiveData<List<Recipe>> = recipeDao.readAllData()

    suspend fun addRecipe(recipe: Recipe){
        recipeDao.addRecipe(recipe)
    }

    fun searchRecipes(query: String): LiveData<List<Recipe>> {
        return recipeDao.searchRecipes("%$query%")
    }
}