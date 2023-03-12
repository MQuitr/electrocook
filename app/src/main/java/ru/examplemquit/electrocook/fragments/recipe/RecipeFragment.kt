package ru.examplemquit.electrocook.fragments.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.room.Update
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

        // Args
        val recipeTitle = binding.recipeTitleRecipeFrag
        val recipeDescription = binding.recipeDescriptionRecipeFrag
        val recipeIngredient = binding.recipeIngredientRecipeFrag
        val recipeStep = binding.recipeStepRecipeFrag

        recipeTitle.setText(args.currentRecipe.title)
        recipeDescription.setText(args.currentRecipe.description)
        recipeIngredient.setText(args.currentRecipe.ingredient)
        recipeStep.setText(args.currentRecipe.steps)


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}