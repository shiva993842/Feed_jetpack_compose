package com.example.feed.SplashScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Canvas
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary
import com.example.feed.ui.theme.PrimaryDark
import com.example.feed.utils.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val context = LocalContext.current

    var startAnimation by remember { mutableStateOf(false) }
    var showText       by remember { mutableStateOf(false) }
    var showBottom     by remember { mutableStateOf(false) }

    val iconScale by animateFloatAsState(
        targetValue   = if (startAnimation) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "scale"
    )
    val textAlpha by animateFloatAsState(
        targetValue   = if (showText) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "text"
    )
    val tagAlpha by animateFloatAsState(
        targetValue   = if (showText) 1f else 0f,
        animationSpec = tween(700, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "tag"
    )
    val bottomAlpha by animateFloatAsState(
        targetValue   = if (showBottom) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "bottom"
    )
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(tween(950, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ps"
    )

    LaunchedEffect(true) {
        delay(200); startAnimation = true
        delay(600); showText = true
        delay(400); showBottom = true
        delay(1500)

        val session = SessionManager(context)
        // If token exists → user already logged in → go directly to Feed
        val destination = if (session.isLoggedIn()) Screen.Feed.route else Screen.Login.route

        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFE5B99F), Color(0xFFFFFFFF), Color(0xFFDCA180))))
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp).alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MADE WITH ♥", color = Primary.copy(alpha = 0.6f),
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 3.sp)
        }

        Column(
            modifier            = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .scale(iconScale * pulseScale)
                    .size(120.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(colors = listOf(Primary, PrimaryDark),
                        start = Offset(0f, 0f), end = Offset(300f, 300f))),
                contentAlignment = Alignment.Center
            ) { CameraIcon() }

            Spacer(modifier = Modifier.height(28.dp))
            Text(text = "Feed", color = Color(0xFF1A1A1A), fontSize = 42.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 4.sp, modifier = Modifier.alpha(textAlpha))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Share your moments", color = Color(0xFF999999),
                fontSize = 14.sp, modifier = Modifier.alpha(tagAlpha))
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 52.dp).alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "from", color = Color(0xFFBBBBBB), fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = "Ronaldo", color = Primary, fontSize = 15.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun CameraIcon() {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w = size.width; val h = size.height; val white = Color.White
        drawRoundRect(color = white, topLeft = Offset(4f, 16f), size = Size(w - 8f, h - 24f), cornerRadius = CornerRadius(16f), alpha = 0.95f)
        drawRoundRect(color = white, topLeft = Offset(w * 0.32f, 6f), size = Size(w * 0.22f, 13f), cornerRadius = CornerRadius(6f), alpha = 0.9f)
        drawCircle(color = white, radius = 5f, center = Offset(w * 0.82f, 26f), alpha = 0.85f)
        drawCircle(color = white, radius = w * 0.28f, center = Offset(w * 0.5f, h * 0.58f), style = Stroke(width = 4f), alpha = 0.9f)
        drawCircle(color = white, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.58f), style = Stroke(width = 3f), alpha = 0.7f)
        drawCircle(color = white, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.58f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}