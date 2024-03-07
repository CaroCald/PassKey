package com.google.credentialmanager.sample.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.credentialmanager.sample.data.models.LoginFinishRequest
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.repository.UserRepository
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.data.models.errors.BasicErrorHandler
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _userResponse: MutableLiveData<UserResponse> = MutableLiveData()
    val userResponse = _userResponse

    private val _finishRegister: MutableLiveData<Boolean> = MutableLiveData()
    val finishRegister = _finishRegister

    private val _finishLoginStart: MutableLiveData<LoginStartResponse> = MutableLiveData()
    val finishRLoginStart = _finishLoginStart

    private val _finishLogin: MutableLiveData<Boolean> = MutableLiveData()
    val finishRLogin = _finishLogin

    private val _errorHandler: MutableLiveData<BasicErrorHandler> = MutableLiveData()
    val errorHandler = _errorHandler

    fun getUserResponse(user: UserRequest) = viewModelScope.launch {
        repository.doAuthenticate(user)
            .collect { values ->
                values.onFailure {
                    _errorHandler.value = it.message?.let { it1 -> BasicErrorHandler(it1) }
                }
                values.map {
                    _userResponse.value = it
                }

            }
    }

    fun getRegisterFinish(registerFinish: RegisterFinish) = viewModelScope.launch {
        repository.doARegisterFinish(registerFinish)
            .collect { values ->
                values.onFailure {
                    _errorHandler.value = it.message?.let { it1 -> BasicErrorHandler(it1) }
                }
                values.map {
                    _finishRegister.value = it
                }
            }
    }

    fun doLoginStart(userRequest: UserRequest) = viewModelScope.launch {
        repository.doLoginStart(userRequest)
            .collect { values ->
                values.onFailure {
                    _errorHandler.value = it.message?.let { it1 -> BasicErrorHandler(it1) }
                }
                values.map {
                    _finishLoginStart.value = it
                }
            }
    }

    fun doLoginFinish(passkeyLoginResponse: LoginFinishRequest) = viewModelScope.launch {
        repository.doLoginFinish(passkeyLoginResponse)

            .collect { values ->
                values.onFailure {
                    _errorHandler.value = it.message?.let { it1 -> BasicErrorHandler(it1) }
                }
                values.map {
                    _finishLogin.value = it
                }
            }
    }
}
