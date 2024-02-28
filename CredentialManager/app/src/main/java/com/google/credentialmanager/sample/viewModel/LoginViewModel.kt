
package com.google.credentialmanager.sample.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
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
}
