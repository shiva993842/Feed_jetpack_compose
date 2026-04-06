package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
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
fun OtpScreen(
    navController : NavController,
    viewModel     : AuthViewModel,
    isLogin       : Boolean = false
) {
    val otpValues       = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var resendTimer     by remember { mutableStateOf(30) }
    var errorMsg        by remember { mutableStateOf("") }

    val otpState by viewModel.otpState.collectAsState()

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        while (resendTimer > 0) {
            kotlinx.coroutines.delay(1000)
            resendTimer--
        }
    }

    LaunchedEffect(otpState) {
        when (otpState) {
            is UiState.Success -> {
                navController.navigate(Screen.Feed.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is UiState.Error -> {
                errorMsg = (otpState as UiState.Error).message
                viewModel.resetOtpState()
            }
            else -> {}
        }
    }

    OtpContent(
        otpValues       = otpValues,
        focusRequesters = focusRequesters,
        resendTimer     = resendTimer,
        errorMsg        = errorMsg,
        isLoading       = otpState is UiState.Loading,
        isLogin         = isLogin,
        onValueChange   = { index, value ->
            if (value.length <= 1 && value.all { it.isDigit() }) {
                otpValues[index] = value
                if (value.isNotEmpty() && index < 5) {
                    focusRequesters[index + 1].requestFocus()
                }
                if (errorMsg.isNotEmpty()) errorMsg = ""
            }
        },
        onVerifyClick   = {
            val code = otpValues.joinToString("")
            if (isLogin) viewModel.verifyLoginOtp(code)
            else viewModel.verifyOtp(code)
        },
        onResendClick   = {
            viewModel.resendOtp()
            resendTimer = 30
            errorMsg    = ""
        },
        onBackClick     = { navController.popBackStack() }
    )
}

// ── Pure UI ───────────────────────────────
@Composable
fun OtpContent(
    otpValues       : List<String>          = List(6) { "" },
    focusRequesters : List<FocusRequester>  = List(6) { FocusRequester() },
    resendTimer     : Int                   = 30,
    errorMsg        : String                = "",
    isLoading       : Boolean               = false,
    isLogin         : Boolean               = false,
    onValueChange   : (Int, String) -> Unit = { _, _ -> },
    onVerifyClick   : () -> Unit            = {},
    onResendClick   : () -> Unit            = {},
    onBackClick     : () -> Unit            = {}
) {
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
            Spacer(Modifier.height(60.dp))

            // ── Back ──────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector        = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = Primary,
                    modifier           = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Icon ──────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📧", fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text       = if (isLogin) "Verify your login"
                else "Verify your email",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text       = "Enter the 6-digit code sent\nto your email address",
                fontSize   = 14.sp,
                color      = Color(0xFF999999),
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(40.dp))

            // ── OTP Boxes ─────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                otpValues.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value         = value,
                        onValueChange = { onValueChange(index, it) },
                        modifier      = Modifier
                            .size(50.dp)
                            .focusRequester(focusRequesters[index]),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign  = TextAlign.Center,
                            fontSize   = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1A1A1A)
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction    = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Primary,
                            unfocusedBorderColor = if (value.isNotEmpty())
                                Primary.copy(alpha = 0.5f)
                            else
                                Color(0xFFDDDDDD),
                            cursorColor          = Primary,
                            focusedTextColor     = Color(0xFF1A1A1A),
                            unfocusedTextColor   = Color(0xFF1A1A1A)
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Error ─────────────────────────
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg,
                    color     = Color(0xFFEF4444),
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(24.dp))

            // ── Verify Button ─────────────────
            Button(
                onClick  = onVerifyClick,
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
                    Text("Verify & Continue",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Resend ────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Didn't receive code? ",
                    color    = Color(0xFF999999),
                    fontSize = 14.sp)
                if (resendTimer > 0) {
                    Text("Resend in ${resendTimer}s",
                        color    = Color(0xFFAAAAAA),
                        fontSize = 14.sp)
                } else {
                    Text("Resend OTP",
                        color      = Primary,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.clickable { onResendClick() })
                }
            }
        }
    }
}

// ── Preview — 1 line! ─────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtpScreenPreview() {
    OtpContent()
}