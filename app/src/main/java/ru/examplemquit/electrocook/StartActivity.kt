package ru.examplemquit.electrocook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import ru.examplemquit.electrocook.fragments.list.ListFragmentDirections

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.fragment_main))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                // переход на окно с избранными рецептами
                val navController = findNavController(R.id.fragment_main)
                when (navController.currentDestination?.id) {
                    R.id.listFragment -> {
                        navController.navigate(R.id.action_listFragment_to_favoriteFragment)
                    }
                    R.id.recipeFragment3 -> {
                        navController.navigate(R.id.action_recipeFragment_to_favoriteFragment)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}