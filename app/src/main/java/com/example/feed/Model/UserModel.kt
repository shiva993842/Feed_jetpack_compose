package com.example.feed.Model

data class UserModel(
    val uid       : String = "",
    val fullName  : String = "",
    val username  : String = "",
    val email     : String = "",
    val phone     : String = "",
    val bio       : String = "",
    val avatarUrl : String = "",
    val followers : Int    = 0,
    val following : Int    = 0,
    val posts     : Int    = 0,
    val createdAt : Long   = 0L
)