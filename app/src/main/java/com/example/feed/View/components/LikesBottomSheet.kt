package com.example.feed.View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.feed.ui.theme.Primary

val dummyLikes = listOf(
    Triple("user1", "ronaldo_23",   "Hyderabad, India"),
    Triple("user2", "shiva_photos", "Mumbai, India"),
    Triple("user3", "travel_vibes", "Goa, India"),
    Triple("user4", "food_lover",   "Chennai, India"),
    Triple("user5", "city_life",    "Bangalore, India"),
    Triple("user6", "marklavern",   "Delhi, India"),
    Triple("user7", "amberberry",   "Pune, India"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikesBottomSheet(
    likesCount : Int        = 0,
    onDismiss  : () -> Unit = {}
) {
    // ✅ skipPartiallyExpanded = false → sheet opens at ~60% (PartiallyExpanded state)
    //    User can then drag up to go fully expanded (like CommentsBottomSheet)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color.White,
        // ✅ Remove fillMaxHeight() — let Material3 handle partial vs full height naturally.
        //    statusBarsPadding() is applied only when fully expanded via sheetState.
        modifier         = Modifier.fillMaxWidth(),
        dragHandle = {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFDDDDDD))
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint               = Color(0xFFEF4444),
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text       = "$likesCount likes",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1A1A1A)
                    )
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            }
        }
    ) {
        // ✅ fillMaxHeight(0.6f) on the content column:
        //    - When PartiallyExpanded → sheet peeks at ~60% screen height
        //    - When user drags up to Expanded → Material3 overrides and goes full height
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.98f)
                .navigationBarsPadding()
        ) {
            LazyColumn(
                modifier       = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(dummyLikes) { (_, username, location) ->
                    LikeUserRow(username = username, location = location)
                }
            }
        }
    }
}

@Composable
fun LikeUserRow(username: String, location: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                color      = Primary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = username, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
            if (location.isNotEmpty()) {
                Text(text = location, fontSize = 12.sp, color = Color(0xFF888888))
            }
        }
        Icon(
            imageVector        = Icons.Filled.Favorite,
            contentDescription = null,
            tint               = Color(0xFFEF4444),
            modifier           = Modifier.size(16.dp)
        )
    }
}