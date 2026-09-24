package com.example.feed.utils

import android.content.Context
import android.content.SharedPreferences

// Saves token & email after login/register so Splash can check if user is already logged in
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("feed_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EMAIL = "user_email"
    }

    fun saveSession(token: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}