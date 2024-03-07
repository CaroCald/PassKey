
package com.google.credentialmanager.sample.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.credentialmanager.sample.data.models.LoginFinishRequest
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.repository.UserRepository
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserRepository
) : ViewModel() {

    private val _userResponseNetwork: MutableLiveData<Result<UserResponse>> = MutableLiveData()
    private val _userResponse: MutableLiveData<UserResponse> = MutableLiveData()
    val userResponse = _userResponse

    private val _finishRegisterNetwork: MutableLiveData<Result<Boolean>> = MutableLiveData()
    private val _finishRegister: MutableLiveData<Boolean> = MutableLiveData()
    val finishRegister = _finishRegister


    private val _finishLoginStartNetwork: MutableLiveData<Result<LoginStartResponse>> = MutableLiveData()
    private val _finishLoginStart: MutableLiveData<LoginStartResponse> = MutableLiveData()
    val finishRLoginStart = _finishLoginStart

    private val _finishLoginNetwork: MutableLiveData<Result<Boolean>> = MutableLiveData()
    private val _finishLogin: MutableLiveData<Boolean> = MutableLiveData()
    val finishRLogin = _finishLogin

    fun getUserResponse(user: UserRequest) = viewModelScope.launch {
        repository.doAuthenticate(user).collect { values ->
            _userResponseNetwork.value = values
            _userResponseNetwork.value?.map {
                _userResponse.value = it
            }
        }
    }

    fun getRegisterFinish(registerFinish: RegisterFinish) = viewModelScope.launch {
        repository.doARegisterFinish(registerFinish).collect { values ->
            _finishRegisterNetwork.value = values
            _finishRegisterNetwork.value?.map {
                _finishRegister.value = it
            }
        }
    }

    fun doLoginStart(userRequest: UserRequest) = viewModelScope.launch {
        repository.doLoginStart(userRequest).collect { values ->
            _finishLoginStartNetwork.value = values
            _finishLoginStartNetwork.value?.map {
                _finishLoginStart.value = it
            }
        }
    }
    fun doLoginFinish( passkeyLoginResponse: LoginFinishRequest) = viewModelScope.launch {
        repository.doLoginFinish(passkeyLoginResponse).collect { values ->
            _finishLoginNetwork.value = values
            _finishLoginNetwork.value?.map {
                _finishLogin.value = it
            }
        }
    }
}
