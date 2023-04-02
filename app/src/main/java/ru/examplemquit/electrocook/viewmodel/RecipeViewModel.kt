package ru.examplemquit.electrocook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.examplemquit.electrocook.data.RecipeDatabase
import ru.examplemquit.electrocook.model.Recipe
import ru.examplemquit.electrocook.repository.RecipeRepository

class RecipeViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Recipe>>
    private val repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        readAllData = repository.readAllData
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecipe(recipe)
        }
    }

    fun searchRecipes(query: String): LiveData<List<Recipe>> {
        return repository.searchRecipes(query)
    }
}