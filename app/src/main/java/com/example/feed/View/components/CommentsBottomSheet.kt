package com.example.feed.View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.feed.ui.theme.Primary

// ── Comment Data ──────────────────────────────
data class CommentModel(
    val id      : String  = "",
    val username: String  = "",
    val text    : String  = "",
    val timeAgo : String  = "",
    val likes   : Int     = 0,
    val isLiked : Boolean = false
)

val dummyComments = listOf(
    CommentModel("1", "marklavern",   "Oh who else was trying to figure out if it was like so ryan 😍", "2d",  847),
    CommentModel("2", "amberberry",   "TAXES IN AMBULANCE 🚨",                                          "1d",  234),
    CommentModel("3", "asho200",      "TAXES IN AMBULANCE 🚨",                                          "23h", 127),
    CommentModel("4", "travel_vibes", "Amazing shot! Where was this taken? 📍",                         "5h",  56),
    CommentModel("5", "city_life",    "Absolutely stunning 🔥🔥🔥",                                     "3h",  89),
    CommentModel("6", "food_lover",   "This is giving me major wanderlust 😭",                           "2h",  43),
    CommentModel("7", "shiva_photos", "The lighting here is 🤌 perfect",                                 "1h",  21),
    CommentModel("8", "ronaldo_23",   "Thanks everyone for the love! 🙏",                                "45m", 312),
    CommentModel("9", "marklavern",   "Oh who else was trying to figure out if it was like so ryan 😍", "2d",  847),
    CommentModel("10", "amberberry",   "TAXES IN AMBULANCE 🚨",                                          "1d",  234),
    CommentModel("11", "asho200",      "TAXES IN AMBULANCE 🚨",                                          "23h", 127),
    CommentModel("12", "travel_vibes", "Amazing shot! Where was this taken? 📍",                         "5h",  56),
    CommentModel("13", "city_life",    "Absolutely stunning 🔥🔥🔥",                                     "3h",  89),
    CommentModel("14", "food_lover",   "This is giving me major wanderlust 😭",                           "2h",  43),
    CommentModel("15", "shiva_photos", "The lighting here is 🤌 perfect",                                 "1h",  21),
    CommentModel("16", "ronaldo_23",   "Thanks everyone for the love! 🙏",                                "45m", 312),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(onDismiss: () -> Unit = {}) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true   // ✅ Skip half state → goes full height instantly
    )
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = Color.White,
        // ✅ Full height from bottom up to just below status bar
//        windowInsets      = WindowInsets(0, 0, 0, 0),
        modifier          = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .statusBarsPadding(),
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
                Text(
                    text       = "Comments",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // ── Comments list ─────────────────
            LazyColumn(
                modifier       = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(dummyComments) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // ── Add Comment Input ─────────────
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Y", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (commentText.isEmpty()) {
                        Text(
                            text     = "Add a comment...",
                            fontSize = 13.sp,
                            color    = Color(0xFFAAAAAA)
                        )
                    }
                    BasicTextField(
                        value         = commentText,
                        onValueChange = { commentText = it },
                        textStyle     = TextStyle(fontSize = 13.sp, color = Color(0xFF1A1A1A)),
                        modifier      = Modifier.fillMaxWidth()
                    )
                }
                if (commentText.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Post",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Primary,
                        modifier   = Modifier.clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { commentText = "" }
                    )
                }
            }
        }
    }
}

// ── Single Comment Row ────────────────────────
@Composable
fun CommentItem(comment: CommentModel) {
    var isLiked   by remember { mutableStateOf(comment.isLiked) }
    var likeCount by remember { mutableStateOf(comment.likes) }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = comment.username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                color      = Primary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = comment.username,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.width(6.dp))
                Text(text = comment.timeAgo, fontSize = 11.sp, color = Color(0xFFAAAAAA))
            }
            Spacer(Modifier.height(2.dp))
            Text(text = comment.text, fontSize = 13.sp, color = Color(0xFF1A1A1A))
            Spacer(Modifier.height(4.dp))
            Text(text = "Reply", fontSize = 11.sp, color = Color(0xFF888888), fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick  = {
                    isLiked   = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like comment",
                    tint               = if (isLiked) Color(0xFFEF4444) else Color(0xFFAAAAAA),
                    modifier           = Modifier.size(14.dp)
                )
            }
            if (likeCount > 0) {
                Text(text = "$likeCount", fontSize = 10.sp, color = Color(0xFFAAAAAA))
            }
        }
    }
}