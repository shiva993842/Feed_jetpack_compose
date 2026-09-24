package com.example.feed.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")

    object Login       : Screen("login")
    object Register    : Screen("register")
    object Feed        : Screen("feed")
    object Explore     : Screen("explore")

    object Upload      : Screen("upload")
    object Reels       : Screen("reels")
    object Profile     : Screen("profile")
    object Messages    : Screen("messages")
    object EditProfile : Screen("edit_profile")
    object Settings    : Screen("settings")
    object Saved       : Screen("saved")


    object ChatScreen : Screen("chat/{userId}/{username}") {
        fun withArgs(userId: String, username: String) = "chat/$userId/$username"
    }

    // OTP takes email as argument: "otp/{email}"
    object Otp : Screen("otp/{email}") {
        fun withEmail(email: String) = "otp/$email"
    }

    object FriendsProfile : Screen("friends_profile/{userId}/{username}") {
        fun withArgs(userId: String, username: String) = "friends_profile/$userId/$username"
    }
}