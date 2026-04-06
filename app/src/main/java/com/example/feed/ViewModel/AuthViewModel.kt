package com.example.feed.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feed.Repository.AuthRepository
import com.example.feed.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // ── States ────────────────────────────────
    private val _loginState    = MutableStateFlow<UiState<String>>(UiState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerState = _registerState.asStateFlow()

    private val _otpState      = MutableStateFlow<UiState<String>>(UiState.Idle)
    val otpState = _otpState.asStateFlow()

    // ── Temp data storage ─────────────────────
    var tempFullName  : String  = ""
    var tempUsername  : String  = ""
    var tempEmail     : String  = ""
    var tempPhone     : String  = ""
    var tempPassword  : String  = ""
    var isLoginFlow   : Boolean = false  // ← Login or Register

    // ── Login — verify credentials + send OTP ─
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Please fill all fields!")
            return
        }
        isLoginFlow  = true     // ← mark as login flow
        tempEmail    = email
        tempPassword = password

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = repository.checkLoginCredentials(email, password)
            _loginState.value = if (result.isSuccess)
                UiState.Success("OTP sent!")
            else
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed!"
                )
        }
    }

    // ── Register — Send OTP ───────────────────
    fun register(
        fullName : String,
        username : String,
        email    : String,
        phone    : String,
        password : String
    ) {
        if (fullName.isBlank() || username.isBlank() ||
            email.isBlank() || phone.isBlank() || password.isBlank()) {
            _registerState.value = UiState.Error("Please fill all fields!")
            return
        }
        if (password.length < 6) {
            _registerState.value = UiState.Error("Password must be 6+ characters!")
            return
        }
        isLoginFlow  = false    // ← mark as register flow
        tempFullName = fullName
        tempUsername = username
        tempEmail    = email
        tempPhone    = phone
        tempPassword = password

        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val result = repository.sendOtp(email, fullName)
            _registerState.value = if (result.isSuccess)
                UiState.Success("OTP sent!")
            else
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to send OTP!"
                )
        }
    }

    // ── Verify OTP — Register flow ────────────
    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp.length != 6) {
            _otpState.value = UiState.Error("Please enter 6 digit code!")
            return
        }
        viewModelScope.launch {
            _otpState.value = UiState.Loading
            val result = repository.verifyOtp(
                email      = tempEmail,
                enteredOtp = enteredOtp,
                fullName   = tempFullName,
                username   = tempUsername,
                phone      = tempPhone,
                password   = tempPassword
            )
            _otpState.value = if (result.isSuccess)
                UiState.Success("Verified!")
            else
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Verification failed!"
                )
        }
    }

    // ── Verify OTP — Login flow ───────────────
    fun verifyLoginOtp(enteredOtp: String) {
        if (enteredOtp.length != 6) {
            _otpState.value = UiState.Error("Please enter 6 digit code!")
            return
        }
        viewModelScope.launch {
            _otpState.value = UiState.Loading
            val result = repository.verifyLoginOtp(
                email      = tempEmail,
                enteredOtp = enteredOtp,
                password   = tempPassword
            )
            _otpState.value = if (result.isSuccess)
                UiState.Success("Login successful!")
            else
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Verification failed!"
                )
        }
    }

    // ── Resend OTP ────────────────────────────
    fun resendOtp() {
        val name = if (isLoginFlow) tempEmail else tempFullName
        viewModelScope.launch {
            _otpState.value = UiState.Loading
            val result = repository.sendOtp(tempEmail, name)
            _otpState.value = if (result.isSuccess)
                UiState.Success("OTP resent!")
            else
                UiState.Error("Failed to resend OTP!")
        }
    }

    // ── Reset States ──────────────────────────
    fun resetLoginState()    { _loginState.value    = UiState.Idle }
    fun resetRegisterState() { _registerState.value = UiState.Idle }
    fun resetOtpState()      { _otpState.value      = UiState.Idle }
}