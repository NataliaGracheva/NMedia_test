package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.FeedModelState
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val apiService: ApiService,
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun authorizationUser(login: String, password: String) {
        viewModelScope.launch {
            _dataState.postValue(FeedModelState(loading = true))
            try {
                val response = apiService.updateUser(login, password)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                _dataState.postValue(FeedModelState())
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(FeedModelState(error = true))
//            } catch (e: Exception) {
//                _dataState.postValue(FeedModelState(loginError = true))
            }
        }
    }
}