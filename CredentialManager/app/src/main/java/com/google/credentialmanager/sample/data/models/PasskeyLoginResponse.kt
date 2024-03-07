package com.google.credentialmanager.sample.data.models

data class PasskeyLoginResponse (
    val rawId: String,
    val authenticatorAttachment: String,
    val type: String,
    val id: String,
    val response: ResponsePasskey,
    val clientExtensionResults: ClientExtensionResultsLogin
)

class ClientExtensionResultsLogin()

data class ResponsePasskey (
    val clientDataJSON: String,
    val authenticatorData: String,
    val signature: String,
    val userHandle: String
)

