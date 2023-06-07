@file:Suppress("DEPRECATION")

package ru.examplemquit.electrocook

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import ru.examplemquit.electrocook.fragments.list.ListFragmentDirections
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel
import ru.examplemquit.electrocook.fragments.recipe.FavoriteFragmentDirections
import ru.examplemquit.electrocook.fragments.recipe.RecipeFragmentDirections

class StartActivity : AppCompatActivity() {

    private var isDarkTheme: Boolean = true
    private val mailSupport: String = "musienko.0613@inbox.ru"
    private val themeMail: String = "Предложение/Баг"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> isDarkTheme = false
            Configuration.UI_MODE_NIGHT_YES -> isDarkTheme = true
        }
        setContentView(R.layout.activity_main)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fragment_main)
        return when (item.itemId) {

            R.id.action_randomRecipe -> {
                val viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
                viewModel.fetchRandomRecipe()
                viewModel.randomRecipe.observe(this, Observer { randomRecipe ->
                    if (randomRecipe != null) {
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

            R.id.action_favorite -> {
                // переход на окно с избранными рецептами
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

            R.id.action_orderCompany -> {
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

           R.id.action_changeTheme -> {
               isDarkTheme = when (isDarkTheme) {
                   true -> {
                       AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                       false
                   }

                   false -> {
                       AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                       true
                   }
               }
               true
           }

           R.id.action_feedBack -> {
               val email = mailSupport
               val subject = themeMail

               val intent = Intent(Intent.ACTION_SEND).apply {
                   type = "text/plain"
                   putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                   putExtra(Intent.EXTRA_SUBJECT, subject)
               }

               if (intent.resolveActivity(packageManager) != null) {
                   val chooser = Intent.createChooser(intent, "Выберите приложение для отправки письма")
                   startActivity(chooser)
               } else {
                   Toast.makeText(applicationContext, "Нету подходящего приложения для отправки письма", Toast.LENGTH_SHORT).show()
               }
               true
           }

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
            else -> super.onOptionsItemSelected(item)
        }
    }
}