package com.example.feed.authentication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.feed.authentication.viewmodel.LoginViewModel
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary
import com.example.feed.utils.UiState

@Composable
fun LoginScreen(
    navController : NavController,
    viewModel     : LoginViewModel
) {
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var errorMsg  by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                // Navigate to OTP, pass email as argument
                navController.navigate(Screen.Otp.withEmail(email))
                viewModel.resetState()
            }
            is UiState.Error -> {
                errorMsg  = (loginState as UiState.Error).message
                isLoading = false
                viewModel.resetState()
            }
            else -> isLoading = false
        }
    }

    LoginContent(
        email            = email,
        password         = password,
        errorMsg         = errorMsg,
        isLoading        = isLoading,
        onEmailChange    = { email    = it },
        onPasswordChange = { password = it },
        onLoginClick     = { viewModel.login(email, password) },
        onRegisterClick  = { navController.navigate(Screen.Register.route) }
    )
}

@Composable
fun LoginContent(
    email            : String  = "",
    password         : String  = "",
    errorMsg         : String  = "",
    isLoading        : Boolean = false,
    onEmailChange    : (String) -> Unit = {},
    onPasswordChange : (String) -> Unit = {},
    onLoginClick     : () -> Unit       = {},
    onRegisterClick  : () -> Unit       = {}
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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("F", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(Modifier.height(20.dp))
            Text("Welcome back", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
            Spacer(Modifier.height(6.dp))
            Text("Login to your account", fontSize = 14.sp, color = Color(0xFF999999))
            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value           = email,
                onValueChange   = onEmailChange,
                label           = { Text("Email address") },
                singleLine      = true,
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                colors          = loginTextFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value                = password,
                onValueChange        = onPasswordChange,
                label                = { Text("Password") },
                singleLine           = true,
                modifier             = Modifier.fillMaxWidth(),
                shape                = RoundedCornerShape(14.dp),
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector        = if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint               = Primary
                        )
                    }
                },
                colors = loginTextFieldColors()
            )

            Spacer(Modifier.height(10.dp))
            Text("Forgot password?", color = Primary, fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End))

            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = Color(0xFFEF4444), fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick  = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled  = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text("  OR  ", color = Color(0xFFAAAAAA), fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick  = { },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                Spacer(Modifier.width(10.dp))
                Text("Continue with Google", fontSize = 15.sp, color = Color(0xFF333333))
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account? ", color = Color(0xFF999999), fontSize = 14.sp)
            Text("Sign up", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onRegisterClick() })
        }
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Primary,
    focusedLabelColor    = Primary,
    cursorColor          = Primary,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedTextColor     = Color(0xFF1A1A1A),
    unfocusedTextColor   = Color(0xFF1A1A1A),
    unfocusedLabelColor  = Color(0xFF999999)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginContent()
}