package com.example.feed.View

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Canvas
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary
import com.example.feed.ui.theme.PrimaryDark
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    var startAnimation by remember { mutableStateOf(false) }
    var showText       by remember { mutableStateOf(false) }
    var showBottom     by remember { mutableStateOf(false) }

    // Logo bounce
    val iconScale by animateFloatAsState(
        targetValue   = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ), label = "scale"
    )

    // Text fade
    val textAlpha by animateFloatAsState(
        targetValue   = if (showText) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "text"
    )

    // Tagline delayed
    val tagAlpha by animateFloatAsState(
        targetValue   = if (showText) 1f else 0f,
        animationSpec = tween(700, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "tag"
    )

    // Bottom fade
    val bottomAlpha by animateFloatAsState(
        targetValue   = if (showBottom) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "bottom"
    )

    // Pulse
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(950, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = "ps"
    )

    LaunchedEffect(true) {
        delay(200); startAnimation = true
        delay(600); showText = true
        delay(400); showBottom = true
        delay(1500)
        val user = FirebaseAuth.getInstance().currentUser
        val dest = if (user != null) Screen.Feed.route else Screen.Login.route
        navController.navigate(dest) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    // ── Full screen with gradient background ──
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE5B99F),   // very light orange top
                        Color(0xFFFFFFFF),   // white middle
                        Color(0xFFDCA180)    // very light orange bottom
                    )
                )
            )
    ) {

        // ── TOP — Made by text ────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
                .alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "MADE WITH ♥",
                color      = Primary.copy(alpha = 0.6f),
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp
            )
        }

        // ── CENTER — Logo + Name + Tagline ────
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Camera Logo
            Box(
                modifier = Modifier
                    .scale(iconScale * pulseScale)
                    .size(120.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Primary, PrimaryDark),
                            start  = Offset(0f, 0f),
                            end    = Offset(300f, 300f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                CameraIcon()
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name
            Text(
                text          = "Feed",
                color         = Color(0xFF1A1A1A),
                fontSize      = 42.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier      = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text       = "Share your moments",
                color      = Color(0xFF999999),
                fontSize   = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier   = Modifier.alpha(tagAlpha)
            )
        }

        // ── BOTTOM — From Ronaldo ─────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .alpha(bottomAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "from",
                color      = Color(0xFFBBBBBB),
                fontSize   = 12.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "Ronaldo",
                color      = Primary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

// ── Camera Icon ───────────────────────────────
@Composable
fun CameraIcon() {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w     = size.width
        val h     = size.height
        val white = Color.White

        // Camera body
        drawRoundRect(
            color        = white,
            topLeft      = Offset(4f, 16f),
            size         = Size(w - 8f, h - 24f),
            cornerRadius = CornerRadius(16f),
            alpha        = 0.95f
        )

        // Viewfinder bump
        drawRoundRect(
            color        = white,
            topLeft      = Offset(w * 0.32f, 6f),
            size         = Size(w * 0.22f, 13f),
            cornerRadius = CornerRadius(6f),
            alpha        = 0.9f
        )

        // Flash dot
        drawCircle(
            color  = white,
            radius = 5f,
            center = Offset(w * 0.82f, 26f),
            alpha  = 0.85f
        )

        // Lens outer ring
        drawCircle(
            color  = white,
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.58f),
            style  = Stroke(width = 4f),
            alpha  = 0.9f
        )

        // Lens middle ring
        drawCircle(
            color  = white,
            radius = w * 0.18f,
            center = Offset(w * 0.5f, h * 0.58f),
            style  = Stroke(width = 3f),
            alpha  = 0.7f
        )

        // Lens center dot
        drawCircle(
            color  = white,
            radius = w * 0.07f,
            center = Offset(w * 0.5f, h * 0.58f)
        )
    }
}

// ── Preview ───────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}
