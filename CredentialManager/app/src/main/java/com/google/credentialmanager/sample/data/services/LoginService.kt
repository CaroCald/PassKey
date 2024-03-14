package com.google.credentialmanager.sample.data.services

import com.google.credentialmanager.sample.data.models.LoginFinishRequest
import com.google.credentialmanager.sample.data.models.LoginStartResponse
import com.google.credentialmanager.sample.data.models.PasskeyLoginResponse
import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.models.SuccessResponse
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
     @POST("/register/start")
     suspend fun doRegisterStart(@Body userRequest: UserRequest) : UserResponse

     @POST("/register/finish")
     suspend fun doRegisterFinish(@Body registerFinish: RegisterFinish) : SuccessResponse

     @POST("/login/start")
     suspend fun doLoginStart(@Body userRequest: UserRequest) : LoginStartResponse

     @POST("/login/finish")
     suspend fun doLoginFinish(@Body passkeyLoginResponse: LoginFinishRequest) : SuccessResponse

}
