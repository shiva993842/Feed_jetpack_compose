package com.example.feed.Model

data class OtpModel(
    val email     : String = "",
    val code      : String = "",
    val expiresAt : Long   = 0L
)