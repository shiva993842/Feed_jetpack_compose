package com.example.feed.View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.feed.ui.theme.Primary

@Composable
fun FeedTopBar(
    onNotificationClick : () -> Unit = {},
    onMessageClick      : () -> Unit = {}   // ✅ Wire this to navController.navigate(Screen.Messages.route)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "Feed",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = Primary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector        = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Notifications",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onMessageClick) {   // ✅ navigates to messages
                    Icon(
                        imageVector        = Icons.Outlined.Message,
                        contentDescription = "Messages",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun FeedTopBarPreview() {
    FeedTopBar()
}