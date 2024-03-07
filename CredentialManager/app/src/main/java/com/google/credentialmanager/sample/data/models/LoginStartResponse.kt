package com.google.credentialmanager.sample.data.models
data class LoginStartResponse (
    val challenge: String,
    val timeout: Long,
    val userVerification: String,
    val rpId: String
)
