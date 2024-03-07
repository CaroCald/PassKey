package com.google.credentialmanager.sample.data.models

data class RegisterKeyResponse (
    val rawId: String,
    val authenticatorAttachment: String,
    val type: String,
    val id: String,
    val response: Response,
    val clientExtensionResults: ClientExtensionResults
)

data class ClientExtensionResults (
    val credProps: CredProps
)

data class CredProps (
    val rk: Boolean
)

data class Response (
    val clientDataJSON: String,
    val attestationObject: String,
    val transports: List<String>,
    val authenticatorData: String,
    val publicKeyAlgorithm: Long,
    val publicKey: String
)

