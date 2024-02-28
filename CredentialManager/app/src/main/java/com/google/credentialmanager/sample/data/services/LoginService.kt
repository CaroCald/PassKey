package com.google.credentialmanager.sample.data.services

import com.google.credentialmanager.sample.data.models.RegisterFinish
import com.google.credentialmanager.sample.data.models.UserRequest
import com.google.credentialmanager.sample.data.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
     @POST("/register/start")
     suspend fun doRegisterStart(@Body userRequest: UserRequest) : UserResponse

     @POST("/register/finish")
     suspend fun doRegisterFinish(@Body registerFinish: RegisterFinish) : Boolean
}
