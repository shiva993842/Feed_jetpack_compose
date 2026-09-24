package com.example.feed.network

import com.example.feed.authentication.model.LoginRequest
import com.example.feed.authentication.model.LoginResponse
import com.example.feed.authentication.model.OtpRequest
import com.example.feed.authentication.model.OtpResponse
import com.example.feed.authentication.model.RegisterRequest
import com.example.feed.authentication.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("verify-otp/")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<OtpResponse>
}