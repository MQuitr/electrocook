package ru.examplemquit.electrocook.data

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.examplemquit.electrocook.model.Recipe

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe_table ORDER BY title")
    fun readAllData(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE title LIKE :query")
    fun searchRecipes(query: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE isFavorite = 1")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipe_table SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateRecipe(recipeId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM recipe_table ORDER BY RANDOM() LIMIT 1")
    fun getRandomRecipe(): Recipe?
}