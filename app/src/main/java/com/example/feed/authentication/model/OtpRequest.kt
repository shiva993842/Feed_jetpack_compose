package com.example.feed.authentication.model

import com.google.gson.annotations.SerializedName

data class OtpRequest(
    @SerializedName("email")    val email   : String,
    @SerializedName("otp_code") val otpCode : String
)