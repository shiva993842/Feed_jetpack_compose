package com.example.feed.navigation

sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Login    : Screen("login")
    object Register : Screen("register")
    object Otp      : Screen("otp")
    object Feed     : Screen("feed")
    object Explore  : Screen("explore")
    object Upload   : Screen("upload")
    object Reels    : Screen("reels")
    object Profile  : Screen("profile")
    object Messages  : Screen("messages")
}