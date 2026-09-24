package com.example.feed.authentication.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success") val success : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("data")    val data    : LoginData?
)

data class LoginData(
    @SerializedName("user")         val user         : UserData,
    @SerializedName("token")        val token        : String,
    @SerializedName("otp_sent")     val otpSent      : Boolean,
    @SerializedName("generated_otp") val generatedOtp : String
)