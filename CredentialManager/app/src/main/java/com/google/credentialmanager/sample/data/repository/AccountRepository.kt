package com.google.credentialmanager.sample.data.repository

import android.content.SharedPreferences
import com.google.credentialmanager.sample.data.models.CreatePasskeyRequest
import com.google.credentialmanager.sample.data.models.GetPasskeyRequest
import com.google.credentialmanager.sample.data.models.GetPasskeyResponseData
import com.google.credentialmanager.sample.data.models.UserData
import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.utils.b64Encode

import com.google.gson.Gson
import java.security.SecureRandom
import javax.inject.Inject


class AccountRepository @Inject constructor (
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
)  {

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

    /**
     * Create the request to create a passkey. From https://w3c.github.io/webauthn/#sctn-sample-registration
     */
     fun getCreatePasskeyRequest(userId: String, email: String): String {
        return gson.toJson(
            CreatePasskeyRequest(
                challenge = generateFidoChallenge(),
                rp = CreatePasskeyRequest.Rp(
                    name = "Dashlane Passkey Demo",
                    id = RELYING_PARTY_ID
                ),
                user = CreatePasskeyRequest.User(
                    id = userId,
                    name = email,
                    displayName = email
                ),
                pubKeyCredParams = listOf(
                    CreatePasskeyRequest.PubKeyCredParams(
                        type = "public-key",
                        alg = -7
                    )
                ),
                timeout = 1800000,
                attestation = "none",
                excludeCredentials = emptyList(),
                authenticatorSelection = CreatePasskeyRequest.AuthenticatorSelection(
                    authenticatorAttachment = "platform",
                    requireResidentKey = false,
                    residentKey = "required",
                    userVerification = "required"
                )
            )
        )
    }
    fun getCreatePasskeyRequestFromServer(userResponse: UserResponse): String {
        return gson.toJson(
            userResponse
        )
    }

    fun fromStringToObject(data: String): GetPasskeyResponseData {
        return gson.fromJson(data, GetPasskeyResponseData::class.java)
    }

    /**
     * Create the request to login with a passkey. From https://w3c.github.io/webauthn/#sctn-sample-authentication
     */
     fun getLoginPasskeyRequest(allowedCredential: List<String> = emptyList()): String {
        return gson.toJson(
            GetPasskeyRequest(
                challenge = generateFidoChallenge(),
                timeout = 1800000,
                userVerification = "required",
                rpId = RELYING_PARTY_ID,
                allowCredentials = allowedCredential.map {
                    GetPasskeyRequest.AllowCredentials(
                        id = it,
                        transports = listOf(),
                        type = "public-key"
                    )
                }
            )
        )
    }

    /**
     * Generates a random challenge for the FIDO request, that should be signed by the authenticator
     */
    private fun generateFidoChallenge(): String {
        val secureRandom = SecureRandom()
        val challengeBytes = ByteArray(32)
        secureRandom.nextBytes(challengeBytes)
        return challengeBytes.b64Encode()
    }

    companion object {
        private const val RELYING_PARTY_ID = "dashlane-passkey-demo.glitch.me"
    }
}