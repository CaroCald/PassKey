package com.google.credentialmanager.sample.data.models

data class LoginFinishRequest(
    val username: String,
    val data: PasskeyLoginResponse,
)
