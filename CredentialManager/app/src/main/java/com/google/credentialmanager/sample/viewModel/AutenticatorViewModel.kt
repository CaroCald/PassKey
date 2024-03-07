package com.google.credentialmanager.sample.viewModel


import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterKeyResponse
import com.google.credentialmanager.sample.data.models.UserRequest

import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.data.repository.AccountRepository

import com.google.gson.Gson
import java.security.SecureRandom

class AutenticatorViewModel (
    private val accountRepository: AccountRepository,
    private val credentialManager: CredentialManager,
    private val gson: Gson
) : ViewModel() {

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

    fun saveUser( userData: UserRequest){
        accountRepository.saveUserAccount(userData)
    }

    fun recoverUserInfo(): UserRequest? {
       return accountRepository.getUserAccount()
    }

}