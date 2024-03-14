package com.google.credentialmanager.sample.viewModel


import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterKeyResponse
import com.google.credentialmanager.sample.data.models.UserData
import com.google.credentialmanager.sample.data.models.UserRequest

import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.data.repository.AccountRepository
import com.google.credentialmanager.sample.utils.b64Decode

import com.google.gson.Gson

import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature

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
    }/**
     * Save the user account to the local database
     */
    fun saveUserAccount(userId: String, userData: UserData) {
        accountRepository.saveUserAccount(userId, userData)
    }

    /**
     * Retrieve the user account from the user id inside the local database
     */
    fun getUserAccount(userId: String): UserData? {
        return accountRepository.getUserAccount(userId)
    }


    /**
     * Check if the signature is valid by signing the clientDataJSON with the public key
     */
     fun verifySignature(responseData: PasskeyLoginResponse, publicKey: PublicKey): Boolean {
        val signature = responseData.response.signature.b64Decode()
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initVerify(publicKey)
        val md = MessageDigest.getInstance("SHA-256")
        val clientDataHash = md.digest(responseData.response.clientDataJSON.b64Decode())
        val signatureBase = responseData.response.authenticatorData.b64Decode() + clientDataHash
        sig.update(signatureBase)
        return sig.verify(signature)
    }

}