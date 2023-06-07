package ru.examplemquit.electrocook.fragments.recipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel
import ru.examplemquit.electrocook.databinding.FragmentListBinding
import ru.examplemquit.electrocook.fragments.list.ListAdapter
import java.util.Locale

class FavoriteFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mRecipeViewModel: RecipeViewModel
    private lateinit var searchView: SearchView
    private lateinit var voiceSearchButton: ImageButton

    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Обработка результата успешного запуска активности
                val data: Intent? = result.data
                val res: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = res?.get(0)
                searchView.setQuery(spokenText, false)
            } else {
                // Обработка результата неудачного запуска активности или отмены
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        // Recycle view
        val adapter = ListAdapter()
        val recyclerView = binding.recycleViewList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // RecipeViewModel
        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        mRecipeViewModel.favoriteRecipes.observe(viewLifecycleOwner, Observer { recipe ->
            adapter.setData(recipe)
        })

        //Search
        searchView = binding.searchView
        voiceSearchButton = binding.voiceSearchButton

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    mRecipeViewModel.searchRecipes(newText).observe(viewLifecycleOwner) {
                        adapter.setData(it)
                    }
                }
                return true
            }
        })

        voiceSearchButton.setOnClickListener {
            startSpeechToText()
        }

        return binding.root
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Продиктуйте название рецепта!")
        someActivityResultLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}