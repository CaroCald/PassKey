package com.google.credentialmanager.sample.data.models

data class UserData(
    val credentialId: String,
    val email: String,
    val publicKey: String,
    val creationDate: Long
)