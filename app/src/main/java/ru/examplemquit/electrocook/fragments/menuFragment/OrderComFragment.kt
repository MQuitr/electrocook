package ru.examplemquit.electrocook.fragments.menuFragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.examplemquit.electrocook.databinding.FragmentOrderComBinding

class orderComFragment : Fragment() {

    private var _binding: FragmentOrderComBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.hide() // Скрытие ActionBar
        _binding = FragmentOrderComBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_order_com, container, false)

        val btnSamokat = binding.buttonSamokat
        val btnPerekrestok = binding.buttonPerekrestok
        val btnSber = binding.buttonSbermarket
        val btnYandex = binding.buttonYandexFood

        btnSamokat.setOnClickListener {
            openProg("ru.sbcs.store&hl=ru&gl=US")
        }

        btnPerekrestok.setOnClickListener {
            openProg("ru.perekrestok.app&hl=ru&gl=US")
        }

        btnSber.setOnClickListener {
            openProg("ru.instamart")
        }

        btnYandex.setOnClickListener {
            openProg("ru.foodfox.client&hl=ru&gl=US")
        }


        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openProg(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        val chooser = Intent.createChooser(intent, "Открыть приложение через") // Диалог выбора приложения

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(chooser)
        } else {
            // Если нет активностей, выводим сообщение об ошибке или предлагаем открыть в браузере
            Toast.makeText(requireContext(), "Не удалось найти подходящее приложение", Toast.LENGTH_SHORT).show()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}