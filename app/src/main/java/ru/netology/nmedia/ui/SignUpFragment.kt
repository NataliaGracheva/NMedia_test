package ru.netology.nmedia.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard
import ru.netology.nmedia.viewmodel.SignUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment: Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        with(binding) {
            buttonSignUp.setOnClickListener {
                let {
                    if (
                        it.editTextFieldLogin.text.isNullOrBlank() ||
                        it.editTextFieldPassword.text.isNullOrBlank() ||
                        it.editTextFieldRepeatPassword.text.isNullOrBlank() ||
                        it.editTextFieldName.text.isNullOrBlank()
                    ) {
                        Toast.makeText(
                            activity,
                            "the field is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (
                            textFieldPassword.editText?.text.toString() ==
                            textFieldRepeatPassword.editText?.text.toString()
                        ) {
                            viewModel.registrationUser(
                                textFieldLogin.editText?.text.toString(),
                                textFieldPassword.editText?.text.toString(),
                                textFieldName.editText?.text.toString()
                            )
                            hideKeyboard(requireView())
                        } else
                            textFieldRepeatPassword.error =
                                "the password is incorrect"
                    }
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri)
                    }
                }
            }

//        binding.circleImageViewProfileSignUp.setOnClickListener {
//            ImagePicker.with(this)
//                .crop()
//                .compress(2048)
//                .provider(ImageProvider.BOTH)
//                .galleryMimeTypes(
//                    arrayOf(
//                        "image/png",
//                        "image/jpeg",
//                        "image/jpg"
//                    )
//                )
//                .createIntent(pickPhotoLauncher::launch)
//        }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }


        viewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentSignUp.isVisible = it.loading
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                return@observe
            }
//            binding.circleImageViewProfileSignUp.setImageURI(it.uri)
        }

        return binding.root
    }
}