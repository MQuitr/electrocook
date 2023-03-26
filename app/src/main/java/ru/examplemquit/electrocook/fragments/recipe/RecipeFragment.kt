package ru.examplemquit.electrocook.fragments.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import ru.examplemquit.electrocook.R
import ru.examplemquit.electrocook.databinding.FragmentRecipeBinding

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

            else -> R.drawable.ic_launcher_foreground
        }

        // Args
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


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}