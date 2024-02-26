
package com.example.passkeyapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.passkeyapp.data.repository.UserRepository
import com.example.passkeyapp.data.models.UserRequest
import com.example.passkeyapp.data.models.UserResponse

class LoginViewModel (private val repository: UserRepository
) : ViewModel() {


    fun getUserResponse(user: UserRequest) = liveData<Result<UserResponse>> {
        emitSource(repository.doAuthenticate(user)
            .asLiveData())
    }
}
