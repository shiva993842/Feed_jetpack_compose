package com.example.feed.authentication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feed.authentication.model.RegisterRequest
import com.example.feed.authentication.model.RegisterResponse
import com.example.feed.authentication.repository.RegistrationRepository
import com.example.feed.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val TAG = "RegisterViewModel"

    private val repository = RegistrationRepository()

    private val _registerState = MutableStateFlow<UiState<RegisterResponse>>(UiState.Idle)
    val registerState: StateFlow<UiState<RegisterResponse>> = _registerState

    fun register(
        firstName : String,
        lastName  : String,
        username  : String,
        email     : String,
        phone     : String,
        password  : String
    ) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading

            val request = RegisterRequest(
                username        = username,
                email           = email,
                password        = password,
                confirmPassword = password,
                firstName       = firstName,
                lastName        = lastName,
                phoneNumber     = phone
            )

            Log.d(TAG, "Register request: $request")
            _registerState.value = repository.register(request)
        }
    }

    fun resetState() {
        _registerState.value = UiState.Idle
    }
}