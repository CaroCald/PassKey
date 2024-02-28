package com.google.credentialmanager.sample.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.credentialmanager.sample.data.repository.UserRepository
import javax.inject.Inject

class ViewModelLoginFactory @Inject constructor(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  LoginViewModel(repository) as T
    }

}