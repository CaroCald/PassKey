package com.google.credentialmanager.sample.viewModel

import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.credentialmanager.sample.data.repository.AccountRepository
import com.google.gson.Gson
import javax.inject.Inject

class ViewModelAutenticatorFactory @Inject constructor(
    private val accountRepository: AccountRepository,
    private val credentialManager: CredentialManager,
    private val gson: Gson
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  AutenticatorViewModel(accountRepository, credentialManager, gson) as T
    }

}