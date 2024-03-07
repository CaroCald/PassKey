package com.google.credentialmanager.sample.viewModel


import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterKeyResponse

import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.data.repository.AccountRepository
import com.google.credentialmanager.sample.state.LoginState
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AutenticatorViewModel (
    private val accountRepository: AccountRepository,
    private val credentialManager: CredentialManager,
    private val gson: Gson
) : ViewModel() {
    private val _state: MutableSharedFlow<LoginState> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: SharedFlow<LoginState> = _state.asSharedFlow()

    fun getResponseInString(userResponse: UserResponse): String {
        return accountRepository.getCreatePasskeyRequestFromServer(userResponse);
    }
    fun getResponseLoginInString(userResponse: LoginStartResponse): String {
        return accountRepository.getLoginFromServer(userResponse);
    }
    fun getResponseInObject(data: String): RegisterKeyResponse {
        return accountRepository.fromStringToObject(data);
    }
    fun fromStringToObjectLoginPassKeyResponse(data: String): PasskeyLoginResponse {
        return accountRepository.fromStringToObjectLoginPassKeyResponse(data);
    }



}