package ru.examplemquit.electrocook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import ru.examplemquit.electrocook.fragments.list.ListFragmentDirections
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel
import ru.examplemquit.electrocook.fragments.recipe.FavoriteFragmentDirections
import ru.examplemquit.electrocook.fragments.recipe.RecipeFragmentDirections
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setupActionBarWithNavController(findNavController(R.id.fragment_main))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment_main)
        return when (item.itemId) {

           R.id.action_about -> {
               when (navController.currentDestination?.id) {
                   R.id.listFragment -> {
                       navController.navigate(R.id.action_listFragment_to_aboutFragment)
                   }
                   R.id.recipeFragment -> {
                       navController.navigate(R.id.action_recipeFragment_to_aboutFragment)
                   }
                   R.id.favoriteFragment -> {
                       navController.navigate(R.id.action_favoriteFragment_to_aboutFragment)
                   }
               }
               true
           }

           R.id.action_orderCompany -> {
                //val navController = findNavController(R.id.fragment_main)
                when (navController.currentDestination?.id) {
                    R.id.listFragment -> {
                        navController.navigate(R.id.action_listFragment_to_orderComFragment)
                    }
                    R.id.recipeFragment -> {
                        navController.navigate(R.id.action_recipeFragment_to_orderComFragment)
                    }
                    R.id.favoriteFragment -> {
                        navController.navigate(R.id.action_favoriteFragment_to_orderComFragment)
                    }
                }
               true
           }

            R.id.action_favorite -> {
                // переход на окно с избранными рецептами
                //val navController = findNavController(R.id.fragment_main)
                when (navController.currentDestination?.id) {
                    R.id.listFragment -> {
                        navController.navigate(R.id.action_listFragment_to_favoriteFragment)
                    }
                    R.id.recipeFragment -> {
                        navController.navigate(R.id.action_recipeFragment_to_favoriteFragment)
                    }
                }
                true
            }
            R.id.action_randomRecipe -> {
                val viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
                viewModel.fetchRandomRecipe()
                viewModel.randomRecipe.observe(this, Observer { randomRecipe ->
                    if (randomRecipe != null) {
                        //val navController = findNavController(R.id.fragment_main)

                        when (navController.currentDestination?.id) {
                            R.id.listFragment -> {
                                navController.navigate(ListFragmentDirections.actionListFragmentToRecipeFragment(randomRecipe))
                            }
                            R.id.recipeFragment -> {
                                navController.navigate(RecipeFragmentDirections.actionRecipeFragmentToListFragment())
                                navController.navigate(ListFragmentDirections.actionListFragmentToRecipeFragment(randomRecipe))
                            }
                            R.id.favoriteFragment -> {
                                navController.navigate(FavoriteFragmentDirections.actionFavoriteFragmentToListFragment())
                                navController.navigate(ListFragmentDirections.actionListFragmentToRecipeFragment(randomRecipe))
                            }
                        }
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

   // override fun onSupportNavigateUp(): Boolean {
   //     val navController = findNavController(R.id.fragment_main)
   //     return navController.navigateUp() || super.onSupportNavigateUp()
    //}
}