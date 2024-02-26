package com.example.passkeyapp.data.models

data class UserResponse (
    val challenge: String,
    val rp: Rp,
    val user: User,
    val pubKeyCredParams: List<PubKeyCredParam>,
    val authenticatorSelection: AuthenticatorSelection
)

data class AuthenticatorSelection (
    val authenticatorAttachment: String,
    val userVerification: String,
    val residentKey: String,
    val requireResidentKey: Boolean
)

data class PubKeyCredParam (
    val type: String,
    val alg: Long
)

data class Rp (
    val id: String,
    val name: String
)

data class User (
    val id: String,
    val name: String,
    val displayName: String
)
