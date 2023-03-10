package ru.examplemquit.electrocook.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel
import ru.examplemquit.electrocook.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRecipeViewModel: RecipeViewModel

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
        mRecipeViewModel.readAllData.observe(viewLifecycleOwner, Observer { recipe ->
            adapter.setData(recipe)
        })

        /*binding.recycleView_list.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_recipeFragment)
        }*/
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}