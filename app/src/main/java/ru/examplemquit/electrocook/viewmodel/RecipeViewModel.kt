package ru.examplemquit.electrocook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.examplemquit.electrocook.data.RecipeDatabase
import ru.examplemquit.electrocook.model.Recipe
import ru.examplemquit.electrocook.repository.RecipeRepository

class RecipeViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Recipe>>
    val favoriteRecipes: LiveData<List<Recipe>>
    private val repository: RecipeRepository
    private val _randomRecipe = MutableLiveData<Recipe?>()
    val randomRecipe: LiveData<Recipe?> get() = _randomRecipe

    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        readAllData = repository.readAllData
        favoriteRecipes = repository.favoriteRecipes
    }

    fun fetchRandomRecipe() {
        viewModelScope.launch (Dispatchers.IO) {
            val randomRecipe = repository.fetchRandomRecipe()
            _randomRecipe.postValue(randomRecipe)
        }
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecipe(recipe)
        }
    }

    fun searchRecipes(query: String): LiveData<List<Recipe>> {
        return repository.searchRecipes(query)
    }

    fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, isFavorite)
        }
    }
}