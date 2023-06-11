package ru.examplemquit.electrocook.fragments.list

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.examplemquit.electrocook.viewmodel.RecipeViewModel
import ru.examplemquit.electrocook.databinding.FragmentListBinding
import java.util.Locale

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mRecipeViewModel: RecipeViewModel
    private lateinit var searchView: SearchView
    private lateinit var voiceSearchButton: ImageButton
    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>

    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    private val RECORD_AUDIO_REQUEST_CODE = 1

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
                Toast.makeText(context, "Что то пошло не так", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show() //Отображение ActionBar
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
            checkRecordAudioPermission()
        }

        return binding.root
    }

    private fun checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), RECORD_AUDIO_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запрашиваем его
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(RECORD_AUDIO_PERMISSION), RECORD_AUDIO_REQUEST_CODE)
        } else {
            // Разрешение уже предоставлено, запускаем распознавание речи
            startSpeechToText()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, запускаем распознавание речи
                startSpeechToText()
            } else {
                // Разрешение не предоставлено, выводим сообщение об ошибке или выполняем другие действия
                Toast.makeText(requireContext(), "Разрешение на доступ к микрофону не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
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