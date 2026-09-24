package com.example.feed.authentication.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username")         val username        : String,
    @SerializedName("email")            val email           : String,
    @SerializedName("password")         val password        : String,
    @SerializedName("confirm_password") val confirmPassword : String,
    @SerializedName("first_name")       val firstName       : String,
    @SerializedName("last_name")        val lastName        : String,
    @SerializedName("phone_number")     val phoneNumber     : String
)