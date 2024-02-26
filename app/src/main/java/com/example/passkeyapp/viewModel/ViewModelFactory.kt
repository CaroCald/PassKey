package com.example.passkeyapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.passkeyapp.data.repository.UserRepository
import javax.inject.Inject

class ViewModelLoginFactory @Inject constructor(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  LoginViewModel(repository) as T
    }

}