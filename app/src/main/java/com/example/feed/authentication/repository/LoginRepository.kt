package com.example.feed.authentication.repository

import android.util.Log
import com.example.feed.authentication.model.LoginRequest
import com.example.feed.authentication.model.LoginResponse
import com.example.feed.network.RetrofitClient
import com.example.feed.utils.UiState

class LoginRepository {

    private val TAG = "LoginRepository"

    suspend fun login(email: String, password: String): UiState<LoginResponse> {
        return try {
            val request = LoginRequest(email = email, password = password)
            Log.d(TAG, "Login Request: $request")

            val response = RetrofitClient.instance.login(request)
            Log.d(TAG, "Login Response Code: ${response.code()}")
            Log.d(TAG, "Login Response Body: ${response.body()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    UiState.Success(body)
                } else {
                    UiState.Error(body?.message ?: "Login failed")
                }
            } else {
                val errBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP Error ${response.code()}: $errBody")
                UiState.Error("Error ${response.code()}: $errBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}", e)
            UiState.Error(e.message ?: "Something went wrong")
        }
    }
}