package com.example.feed.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FeedColorScheme = darkColorScheme(
    primary       = Primary,
    background    = Background,
    surface       = Surface,
    onPrimary     = TextPrimary,
    onBackground  = TextPrimary,
    onSurface     = TextPrimary,
)

@Composable
fun FeedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FeedColorScheme,
        typography  = Typography,
        content     = content
    )
}