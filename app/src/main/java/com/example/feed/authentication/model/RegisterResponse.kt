package com.example.feed.authentication.model

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("success") val success : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("data")    val data    : RegisterData?
)

data class RegisterData(
    @SerializedName("user")         val user         : UserData,
    @SerializedName("token")        val token        : String,
    @SerializedName("otp_sent")     val otpSent      : Boolean,
    @SerializedName("generated_otp") val generatedOtp : String
)

data class UserData(
    @SerializedName("id")              val id             : Int,
    @SerializedName("username")        val username       : String,
    @SerializedName("email")           val email          : String,
    @SerializedName("first_name")      val firstName      : String,
    @SerializedName("last_name")       val lastName       : String,
    @SerializedName("full_name")       val fullName       : String,
    @SerializedName("profile_picture") val profilePicture : String?
)