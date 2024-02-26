package com.example.passkeyapp.data.services

import com.example.passkeyapp.data.models.UserRequest
import com.example.passkeyapp.data.models.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
     @POST("/register/start")
     suspend fun doRegisterStart(@Body userRequest: UserRequest) : UserResponse

     @POST("/register/finish")
     suspend fun doRegisterFinish(@Body userRequest: UserRequest) : UserResponse
}
