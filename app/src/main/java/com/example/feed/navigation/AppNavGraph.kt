package com.example.feed.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feed.View.FeedScreen
import com.example.feed.SplashScreen.SplashScreen
import com.example.feed.View.ChatScreen
import com.example.feed.MyProfile.EditProfileScreen
import com.example.feed.View.ExploreScreen
import com.example.feed.View.MessagesScreen
import com.example.feed.MyProfile.ProfileScreen
import com.example.feed.View.FriendsProfileScreen
import com.example.feed.View.ReelsScreen
import com.example.feed.View.SavedScreen
import com.example.feed.View.SettingsScreen
import com.example.feed.authentication.view.LoginScreen
import com.example.feed.authentication.view.OtpScreen
import com.example.feed.authentication.view.RegisterScreen
import com.example.feed.authentication.viewmodel.LoginViewModel
import com.example.feed.authentication.viewmodel.OtpViewModel
import com.example.feed.authentication.viewmodel.RegisterViewModel
import com.example.feed.uploadpost.view.UploadScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(navController = navController, viewModel = registerViewModel)
        }

        // OTP receives email as a nav argument
        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email        = backStackEntry.arguments?.getString("email") ?: ""
            val otpViewModel : OtpViewModel = viewModel()
            OtpScreen(navController = navController, viewModel = otpViewModel, email = email)
        }
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
        composable(Screen.Messages.route) {
            MessagesScreen(navController = navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    // ── Saved Screen ──────────────────────────────────────────────────
        composable(Screen.Saved.route) {
            SavedScreen(navController = navController)
        }
        composable(
            route = Screen.ChatScreen.route,
            arguments = listOf(
                navArgument("userId")   { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId   = backStackEntry.arguments?.getString("userId")   ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""
            ChatScreen(navController = navController, userId = userId, username = username)
        }
        // ── Friends Profile Screen ────────────────────────────────────────
        composable(
            route     = Screen.FriendsProfile.route,
            arguments = listOf(
                navArgument("userId")   { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId   = backStackEntry.arguments?.getString("userId")   ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""
            FriendsProfileScreen(
                navController = navController,
                userId        = userId,
                username      = username
            )
        }

    }
}