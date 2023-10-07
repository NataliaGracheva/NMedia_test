package ru.netology.nmedia.ui

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.view.load

class ImageFragment: Fragment() {
    companion object {
        var Bundle.urlArg: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        binding.imageFullScreen.load("${BuildConfig.BASE_URL}/media/${arguments?.urlArg}")

        binding.imageFullScreen.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}