package com.example.feed.Model

data class PostModel(
    val postId        : String       = "",
    val userId        : String       = "",
    val username      : String       = "",
    val userAvatar    : String       = "",
    val imageUrl      : String       = "",
    val caption       : String       = "",
    val location      : String       = "",
    val likes         : List<String> = emptyList(),
    val commentsCount : Int          = 0,
    val timestamp     : Long         = 0L
)