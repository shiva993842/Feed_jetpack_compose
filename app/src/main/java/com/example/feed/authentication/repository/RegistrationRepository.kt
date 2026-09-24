package com.example.feed.authentication.repository

import com.example.feed.authentication.model.RegisterRequest
import com.example.feed.authentication.model.RegisterResponse
import com.example.feed.network.RetrofitClient
import com.example.feed.utils.UiState
import org.json.JSONObject
import java.io.IOException

class RegistrationRepository {

    private val api = RetrofitClient.instance

    suspend fun register(request: RegisterRequest): UiState<RegisterResponse> {
        return try {
            val response = api.register(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                if (body.success) {
                    UiState.Success(body)
                } else {
                    UiState.Error(body.message)
                }
            } else {
                // Try to read the server's error message (e.g. "Email already exists")
                val serverMessage = try {
                    response.errorBody()?.string()?.let { JSONObject(it).optString("message") }
                } catch (e: Exception) {
                    null
                }
                UiState.Error(
                    if (!serverMessage.isNullOrBlank()) serverMessage
                    else "Registration failed (${response.code()})"
                )
            }
        } catch (e: IOException) {
            UiState.Error("No internet connection. Please try again.")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Something went wrong")
        }
    }
}