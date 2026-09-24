package com.example.feed.authentication.repository

import android.util.Log
import com.example.feed.authentication.model.OtpRequest
import com.example.feed.authentication.model.OtpResponse
import com.example.feed.network.RetrofitClient
import com.example.feed.utils.UiState

class OtpRepository {

    private val TAG = "OtpRepository"

    suspend fun verifyOtp(email: String, otpCode: String): UiState<OtpResponse> {
        return try {
            val request = OtpRequest(email = email, otpCode = otpCode)
            Log.d(TAG, "OTP Request: $request")

            val response = RetrofitClient.instance.verifyOtp(request)
            Log.d(TAG, "OTP Response Code: ${response.code()}")
            Log.d(TAG, "OTP Response Body: ${response.body()}")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    UiState.Success(body)
                } else {
                    UiState.Error(body?.message ?: "OTP verification failed")
                }
            } else {
                val errBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP Error ${response.code()}: $errBody")
                UiState.Error("Invalid OTP. Please try again.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}", e)
            UiState.Error(e.message ?: "Something went wrong")
        }
    }
}