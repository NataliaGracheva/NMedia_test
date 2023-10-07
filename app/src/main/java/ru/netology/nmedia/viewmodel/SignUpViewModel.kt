package ru.netology.nmedia.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val apiService: ApiService,
) : ViewModel() {
    val data = MutableLiveData<Token>()

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun registrationUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            _dataState.postValue(FeedModelState(loading = true))
            try {
//                val response = apiService.registrationUser(
//                    login.toRequestBody("text/plain".toMediaType()),
//                    password.toRequestBody("text/plain".toMediaType()),
//                    name.toRequestBody("text/plain".toMediaType()),
//                    photo.value?.uri?.toFile()?.let {
//                        val upload = PhotoUpload(it)

//                        MultipartBody.Part.createFormData(
//                            "file",
//                            upload.file.name,
//                            upload.file.asRequestBody()
//                        )
//                    }
//                )
                val file = photo.value?.uri?.toFile()
                val response = if (file != null) {
                    val media = MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        file.asRequestBody()
                    )
                    apiService.registrationUser(
                        login = login.toRequestBody("text/plain".toMediaType()),
                        pass = password.toRequestBody("text/plain".toMediaType()),
                        name = name.toRequestBody("text/plain".toMediaType()),
                        image = media
                    )
                } else {
                        apiService.registrationUser(
                            login,
                            password,
                            name
                        )
                    }

                if (!response.isSuccessful) {
                    Log.d("AUTH", response.message())
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                data.value = Token(body.id, body.token)
                _dataState.postValue(FeedModelState())
            } catch (e: IOException) {
                _dataState.postValue(FeedModelState(error = true))
//            } catch (e: Exception) {
//                throw UnknownError()
            }
        }
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri, null, AttachmentType.IMAGE)
    }
}