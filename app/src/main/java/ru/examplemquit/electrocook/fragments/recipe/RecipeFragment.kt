package ru.examplemquit.electrocook.fragments.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.examplemquit.electrocook.R
import ru.examplemquit.electrocook.databinding.FragmentRecipeBinding
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel

class RecipeFragment : Fragment() {

    private val args by navArgs<RecipeFragmentArgs>()

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)

        val resourceImageId = when (args.currentRecipe.imageResourceId) {
            1 -> R.drawable.american_pancake
            2 -> R.drawable.slice_blin
            3 -> R.drawable.brusketta_s_pomidorami
            4 -> R.drawable.classic_sharlotka
            5 -> R.drawable.oyakodon
            6 -> R.drawable.crem_sup_so_slivkamy
            7 -> R.drawable.kotleety_s_morkovkoy
            else -> R.drawable.ic_launcher_foreground
        }

        // Args
        val btnFavorite = binding.btnFavoriteRecipe
        val viewModel: RecipeViewModel by viewModels()
        val recipeTitle = binding.recipeTitleRecipeFrag
        val recipeDescription = binding.recipeDescriptionRecipeFrag
        val recipeIngredient = binding.recipeIngredientRecipeFrag
        val recipeStep = binding.recipeStepRecipeFrag
        val imageRecipe = binding.imageViewImage

        recipeTitle.text = args.currentRecipe.title
        imageRecipe.setImageResource(resourceImageId)
        recipeDescription.text = args.currentRecipe.description
        recipeIngredient.text = args.currentRecipe.ingredient
        recipeStep.text = args.currentRecipe.steps

        btnFavorite.text = when (!args.currentRecipe.isFavorite) {
            true -> "Добавить в избранное"
            false -> "Удалить из избранных"
        }

        btnFavorite.setOnClickListener {
            viewModel.toggleFavorite(args.currentRecipe.id, !args.currentRecipe.isFavorite)
            args.currentRecipe.isFavorite = !args.currentRecipe.isFavorite
            when (!args.currentRecipe.isFavorite) {
                true -> btnFavorite.text = "Добавить в избранное"
                false -> btnFavorite.text = "Удалить из избранных"
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}