package com.google.credentialmanager.sample.data.repository

import android.content.SharedPreferences
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterKeyResponse
import com.google.credentialmanager.sample.data.models.UserData
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.utils.b64Decode
import com.google.gson.Gson
import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature
import javax.inject.Inject


class AccountRepository @Inject constructor (
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
)  {

    /**
     * Save the user account to the local database
     */
     fun saveUserAccount( userData: UserRequest) {
        with(sharedPreferences.edit()) {
            putString("userId", gson.toJson(userData))
            apply()
        }
    }

    /**
     * Retrieve the user account from the user id inside the local database
     */
     fun getUserAccount(): UserRequest? {

        return gson.fromJson(sharedPreferences.getString("userId", null), UserRequest::class.java)
    }

    /**
     * Save the user account to the local database
     */
    fun saveUserAccount(userId: String, userData: UserData) {
        with(sharedPreferences.edit()) {
            putString(userId, gson.toJson(userData))
            apply()
        }
    }

    /**
     * Retrieve the user account from the user id inside the local database
     */
    fun getUserAccount(userId: String): UserData? {
        return gson.fromJson(sharedPreferences.getString(userId, null), UserData::class.java)
    }

    /**
     * Search if this email is already registered in the local database
     */
     fun searchUserWithEmail(email: String): UserData? {
        val allUsers = sharedPreferences.all
        // Find user with the email that exists in the local database
        return allUsers.values
            .map { gson.fromJson(it as String, UserData::class.java) }
            .find {
                it.email == email
            }
    }

    fun getCreatePasskeyRequestFromServer(userResponse: UserResponse): String {
        return gson.toJson(
            userResponse
        )
    }
    fun getLoginFromServer(userResponse: LoginStartResponse): String {
        return gson.toJson(
            userResponse
        )
    }

    fun fromStringToObject(data: String): RegisterKeyResponse {
        return gson.fromJson(data, RegisterKeyResponse::class.java)
    }

    fun fromStringToObjectLoginPassKeyResponse(data: String): PasskeyLoginResponse {
        return gson.fromJson(data, PasskeyLoginResponse::class.java)
    }



}