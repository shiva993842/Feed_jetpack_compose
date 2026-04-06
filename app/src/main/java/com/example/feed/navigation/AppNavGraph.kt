package com.example.feed.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feed.View.ChatScreen
import com.example.feed.View.ExploreScreen
import com.example.feed.View.FeedScreen
import com.example.feed.View.LoginScreen
import com.example.feed.View.MessagesScreen
import com.example.feed.View.OtpScreen
import com.example.feed.View.ProfileScreen
import com.example.feed.View.ReelsScreen
import com.example.feed.View.RegisterScreen
import com.example.feed.View.SplashScreen
import com.example.feed.View.UploadScreen
import com.example.feed.ViewModel.AuthViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel : AuthViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {
        // ── Auth Flow ─────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel     = authViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                viewModel     = authViewModel
            )
        }
        composable(Screen.Otp.route) {
            OtpScreen(
                navController = navController,
                viewModel     = authViewModel,
                isLogin       = authViewModel.isLoginFlow
            )
        }

        // ── Main App Flow ─────────────────────
        composable(Screen.Feed.route) {
            FeedScreen(navController = navController)
        }
        composable(Screen.Explore.route) {
            ExploreScreen(navController = navController)
        }
        composable(Screen.Upload.route) {
            UploadScreen(navController = navController)
        }
        composable(Screen.Reels.route) {
            ReelsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // ── Messages & Chat ───────────────────
        composable(Screen.Messages.route) {
            MessagesScreen(navController = navController)
        }
        composable("chat/{userId}/{username}") { backStackEntry ->
            val userId   = backStackEntry.arguments?.getString("userId")   ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ChatScreen(
                navController = navController,
                userId        = userId,
                username      = username
            )
        }
    }
}