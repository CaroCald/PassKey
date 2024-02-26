package com.example.passkeyapp.data.repository

import com.example.passkeyapp.data.models.UserRequest
import com.example.passkeyapp.data.models.UserResponse
import com.example.passkeyapp.data.models.errors.BasicErrorHandler
import com.example.passkeyapp.data.services.LoginService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val loginService: LoginService
) {
    fun doAuthenticate(userRequest: UserRequest) : Flow<Result<UserResponse>> {
        return  flow {
            emit(Result.success(loginService.doRegisterStart(userRequest)))

        }.catch { error ->
            emit(Result.failure(BasicErrorHandler(error)))
        }
    }

}
