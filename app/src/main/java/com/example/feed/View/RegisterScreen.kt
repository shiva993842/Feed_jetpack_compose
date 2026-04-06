package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.feed.ViewModel.AuthViewModel
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary
import com.example.feed.utils.UiState

// ── Real Screen ───────────────────────────
@Composable
fun RegisterScreen(
    navController : NavController,
    viewModel     : AuthViewModel
) {
    var fullName    by remember { mutableStateOf("") }
    var username    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var phone       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var errorMsg    by remember { mutableStateOf("") }
    var isLoading   by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                navController.navigate(Screen.Otp.route)
                viewModel.resetRegisterState()
            }
            is UiState.Error -> {
                errorMsg  = (registerState as UiState.Error).message
                isLoading = false
                viewModel.resetRegisterState()
            }
            is UiState.Loading -> isLoading = true
            else -> isLoading = false
        }
    }

    RegisterContent(
        fullName         = fullName,
        username         = username,
        email            = email,
        phone            = phone,
        password         = password,
        errorMsg         = errorMsg,
        isLoading        = isLoading,
        onFullNameChange = { fullName = it },
        onUsernameChange = { username = it },
        onEmailChange    = { email    = it },
        onPhoneChange    = { phone    = it },
        onPasswordChange = { password = it },
        onRegisterClick  = {
            viewModel.register(
                fullName, username,
                email, phone, password
            )
        },
        onLoginClick     = { navController.popBackStack() }
    )
}

// ── Pure UI ───────────────────────────────
@Composable
fun RegisterContent(
    fullName         : String  = "",
    username         : String  = "",
    email            : String  = "",
    phone            : String  = "",
    password         : String  = "",
    errorMsg         : String  = "",
    isLoading        : Boolean = false,
    onFullNameChange : (String) -> Unit = {},
    onUsernameChange : (String) -> Unit = {},
    onEmailChange    : (String) -> Unit = {},
    onPhoneChange    : (String) -> Unit = {},
    onPasswordChange : (String) -> Unit = {},
    onRegisterClick  : () -> Unit       = {},
    onLoginClick     : () -> Unit       = {}
) {
    var passVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // ── Logo ─────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("F", color = Color.White,
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(20.dp))

            Text("Create account",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A))

            Spacer(Modifier.height(6.dp))

            Text("Sign up to get started",
                fontSize = 14.sp,
                color    = Color(0xFF999999))

            Spacer(Modifier.height(32.dp))

            // ── Full Name ─────────────────────
            OutlinedTextField(
                value         = fullName,
                onValueChange = onFullNameChange,
                label         = { Text("Full name") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor     = Color(0xFF1A1A1A),
                    unfocusedTextColor   = Color(0xFF1A1A1A),
                    unfocusedLabelColor  = Color(0xFF999999)
                )
            )

            Spacer(Modifier.height(14.dp))

            // ── Username ──────────────────────
            OutlinedTextField(
                value         = username,
                onValueChange = onUsernameChange,
                label         = { Text("Username") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor     = Color(0xFF1A1A1A),
                    unfocusedTextColor   = Color(0xFF1A1A1A),
                    unfocusedLabelColor  = Color(0xFF999999)
                )
            )

            Spacer(Modifier.height(14.dp))

            // ── Email ─────────────────────────
            OutlinedTextField(
                value         = email,
                onValueChange = onEmailChange,
                label         = { Text("Email address") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor     = Color(0xFF1A1A1A),
                    unfocusedTextColor   = Color(0xFF1A1A1A),
                    unfocusedLabelColor  = Color(0xFF999999)
                )
            )

            Spacer(Modifier.height(14.dp))

            // ── Phone ─────────────────────────
            OutlinedTextField(
                value         = phone,
                onValueChange = onPhoneChange,
                label         = { Text("Phone number") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction    = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor     = Color(0xFF1A1A1A),
                    unfocusedTextColor   = Color(0xFF1A1A1A),
                    unfocusedLabelColor  = Color(0xFF999999)
                )
            )

            Spacer(Modifier.height(14.dp))

            // ── Password ──────────────────────
            OutlinedTextField(
                value         = password,
                onValueChange = onPasswordChange,
                label         = { Text("Password") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                visualTransformation = if (passVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible)
                                Icons.Filled.VisibilityOff
                            else
                                Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor     = Color(0xFF1A1A1A),
                    unfocusedTextColor   = Color(0xFF1A1A1A),
                    unfocusedLabelColor  = Color(0xFF999999)
                )
            )

            // ── Error ─────────────────────────
            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg,
                    color    = Color(0xFFEF4444),
                    fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))

            // ── Register Button ───────────────
            Button(
                onClick  = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape   = RoundedCornerShape(14.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Login Link ────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Already have an account? ",
                    color    = Color(0xFF999999),
                    fontSize = 14.sp)
                Text("Login",
                    color      = Primary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onLoginClick() })
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Preview — 1 line! ─────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterContent()
}