package com.example.feed.authentication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feed.authentication.model.OtpResponse
import com.example.feed.authentication.repository.OtpRepository
import com.example.feed.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {

    private val TAG        = "OtpViewModel"
    private val repository = OtpRepository()

    private val _otpState = MutableStateFlow<UiState<OtpResponse>>(UiState.Idle)
    val otpState: StateFlow<UiState<OtpResponse>> = _otpState

//    fun verifyOtp(email: String, otpCode: String) {
//        viewModelScope.launch {
//            _otpState.value = UiState.Loading
//            Log.d(TAG, "verifyOtp() called — email: $email, otp: $otpCode")
//            _otpState.value = repository.verifyOtp(email, otpCode)
//        }
//    }
fun verifyOtp(email: String, otpCode: String) {
    viewModelScope.launch {
        _otpState.value = UiState.Loading

        // ✅ STATIC BYPASS — remove this block when Firebase is fixed
        if (email == "test@test.com" && otpCode == "123456") {
            _otpState.value = UiState.Success(OtpResponse(success = true, message = "OK",email="test@test.com"))
            return@launch
        }

        Log.d(TAG, "verifyOtp() called — email: $email, otp: $otpCode")
        _otpState.value = repository.verifyOtp(email, otpCode)
    }
}

    fun resetState() {
        _otpState.value = UiState.Idle
    }
}