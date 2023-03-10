package ru.examplemquit.electrocook.repository

import androidx.lifecycle.LiveData
import ru.examplemquit.electrocook.data.RecipeDao
import ru.examplemquit.electrocook.model.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {
    val readAllData: LiveData<List<Recipe>> = recipeDao.readAllData()

    suspend fun addRecipe(recipe: Recipe){
        recipeDao.addRecipe(recipe)
    }
}