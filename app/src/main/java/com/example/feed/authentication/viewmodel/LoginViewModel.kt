package com.example.feed.authentication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.feed.authentication.model.LoginResponse
import com.example.feed.authentication.repository.LoginRepository
import com.example.feed.utils.SessionManager
import com.example.feed.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG        = "LoginViewModel"
    private val repository = LoginRepository()
    private val session    = SessionManager(application)

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            _loginState.value = UiState.Loading
//            Log.d(TAG, "login() called — email: $email")
//            val result = repository.login(email, password)
//            // Save token & email on success so OTP screen can use them
//            if (result is UiState.Success) {
//                val data = result.data.data
//                if (data != null) {
//                    session.saveSession(token = data.token, email = email)
//                    Log.d(TAG, "Session saved — token: ${data.token}")
//                }
//            }
//            _loginState.value = result
//        }
//    }
fun login(email: String, password: String) {
    viewModelScope.launch {
        _loginState.value = UiState.Loading

        // ✅ STATIC BYPASS — remove this block when Firebase is fixed
        if (email == "test@test.com" && password == "123456") {
            _loginState.value = UiState.Success(LoginResponse(success = true, message = "OK", data = null))
            return@launch
        }

        Log.d(TAG, "login() called — email: $email")
        val result = repository.login(email, password)
        if (result is UiState.Success) {
            val data = result.data.data
            if (data != null) {
                session.saveSession(token = data.token, email = email)
            }
        }
        _loginState.value = result
    }
}
    fun resetState() {
        _loginState.value = UiState.Idle
    }
}