package com.google.credentialmanager.sample.data.repository

import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import com.google.credentialmanager.sample.data.models.errors.BasicErrorHandler
import com.google.credentialmanager.sample.data.services.LoginService
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
    fun doARegisterFinish(registerFinish: RegisterFinish) : Flow<Result<Boolean>> {
        return  flow {
            emit(Result.success(loginService.doRegisterFinish(registerFinish)))

        }.catch { error ->
            emit(Result.failure(BasicErrorHandler(error)))
        }
    }

}
