package com.example.feed.authentication.model

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("success") val success : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("email")   val email   : String
)