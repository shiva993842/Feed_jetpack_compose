package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.feed.ui.theme.Primary

// ── Chat message model ────────────────────────
sealed class ChatMessage(
    open val id        : String,
    open val isMe      : Boolean,
    open val timeStamp : String
) {
    data class TextMsg(
        override val id        : String,
        override val isMe      : Boolean,
        override val timeStamp : String,
        val text               : String
    ) : ChatMessage(id, isMe, timeStamp)

    data class ImageMsg(
        override val id        : String,
        override val isMe      : Boolean,
        override val timeStamp : String,
        val imageUrl           : String,
        val caption            : String = ""
    ) : ChatMessage(id, isMe, timeStamp)

    data class VideoMsg(
        override val id        : String,
        override val isMe      : Boolean,
        override val timeStamp : String,
        val thumbnailUrl       : String,
        val duration           : String = "0:30"
    ) : ChatMessage(id, isMe, timeStamp)

    data class SharedPost(
        override val id        : String,
        override val isMe      : Boolean,
        override val timeStamp : String,
        val imageUrl           : String,
        val postCaption        : String,
        val postUser           : String
    ) : ChatMessage(id, isMe, timeStamp)

    data class LikeMsg(
        override val id        : String,
        override val isMe      : Boolean,
        override val timeStamp : String
    ) : ChatMessage(id, isMe, timeStamp)
}

// ── Static dummy chat messages ────────────────
val dummyChatMessages = listOf(
    ChatMessage.TextMsg(  "m1",  isMe = false, "10:01 AM", "Hey! How are you? 👋"),
    ChatMessage.TextMsg(  "m2",  isMe = true,  "10:02 AM", "I'm great! Just got back from the hike 🏔️"),
    ChatMessage.TextMsg(  "m3",  isMe = false, "10:02 AM", "Oh nice! Share some pics?"),
    ChatMessage.ImageMsg( "m4",  isMe = true,  "10:04 AM", "https://picsum.photos/seed/chat1/400/400", "The view from the top 🌄"),
    ChatMessage.TextMsg(  "m5",  isMe = false, "10:05 AM", "Wow that's absolutely stunning! 😍"),
    ChatMessage.TextMsg(  "m6",  isMe = false, "10:05 AM", "Which trail did you take?"),
    ChatMessage.TextMsg(  "m7",  isMe = true,  "10:07 AM", "The north ridge trail. It's 12km but worth every step"),
    ChatMessage.VideoMsg( "m8",  isMe = true,  "10:08 AM", "https://picsum.photos/seed/chatvid1/400/300", "1:24"),
    ChatMessage.TextMsg(  "m9",  isMe = false, "10:10 AM", "That video is amazing! You should post it 🔥"),
    ChatMessage.LikeMsg(  "m10", isMe = false, "10:11 AM"),
    ChatMessage.SharedPost("m11", isMe = false, "10:12 AM",
        imageUrl     = "https://picsum.photos/seed/shared1/400/400",
        postCaption  = "Check this post out! Similar view 😮",
        postUser     = "travel_vibes"
    ),
    ChatMessage.TextMsg(  "m12", isMe = true,  "10:14 AM", "Oh wow! That's almost the same spot 😲"),
    ChatMessage.TextMsg(  "m13", isMe = false, "10:15 AM", "We should go together next time!"),
    ChatMessage.TextMsg(  "m14", isMe = true,  "10:15 AM", "100%! Let's plan it for next weekend 🙌"),
    ChatMessage.ImageMsg( "m15", isMe = false, "10:17 AM", "https://picsum.photos/seed/chat2/400/400", "My last trip there"),
    ChatMessage.TextMsg(  "m16", isMe = true,  "10:18 AM", "Beautiful! 🌿 Can't wait!"),
    ChatMessage.TextMsg(  "m17", isMe = false, "10:18 AM", "😊❤️"),
)

@Composable
fun ChatScreen(navController: NavController, userId: String, username: String) {
    ChatContent(
        username    = username,
        avatarUrl   = "https://picsum.photos/seed/avatar${userId.takeLast(1)}/150/150",
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
fun ChatContent(
    username    : String     = "ronaldo_23",
    avatarUrl   : String     = "https://picsum.photos/seed/avatar1/150/150",
    onBackClick : () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val listState   = rememberLazyListState()

    LaunchedEffect(dummyChatMessages.size) {
        listState.scrollToItem(dummyChatMessages.size - 1)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor      = Color.White,
        topBar = {
            ChatTopBar(
                username    = username,
                avatarUrl   = avatarUrl,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // ── Messages list ─────────────────
            LazyColumn(
                state          = listState,
                modifier       = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(dummyChatMessages) { message ->
                    ChatMessageItem(message = message)
                }
            }

            // ── Input bar ─────────────────────
            ChatInputBar(
                text       = messageText,
                onTextChange = { messageText = it },
                onSend     = { messageText = "" }
            )
        }
    }
}

// ── Chat Top Bar ──────────────────────────────

@Composable
fun ChatTopBar(
    username    : String,
    avatarUrl   : String,
    onBackClick : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = Color(0xFF1A1A1A)
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model              = avatarUrl,
                    contentDescription = username,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = username,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
                Text(
                    text     = "Active now",
                    fontSize = 12.sp,
                    color    = Color(0xFF44D87A)
                )
            }
            Row {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector        = Icons.Outlined.Phone,
                        contentDescription = "Call",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector        = Icons.Outlined.Videocam,
                        contentDescription = "Video call",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

// ── Individual chat message ───────────────────

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isMe = message.isMe

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        when (message) {
            is ChatMessage.TextMsg -> {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart     = if (isMe) 18.dp else 4.dp,
                                topEnd       = if (isMe) 4.dp else 18.dp,
                                bottomStart  = 18.dp,
                                bottomEnd    = 18.dp
                            )
                        )
                        .background(if (isMe) Primary else Color(0xFFF0F0F0))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text     = message.text,
                        fontSize = 14.sp,
                        color    = if (isMe) Color.White else Color(0xFF1A1A1A)
                    )
                }
            }

            is ChatMessage.ImageMsg -> {
                Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model              = message.imageUrl,
                            contentDescription = "Image",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    }
                    if (message.caption.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text     = message.caption,
                            fontSize = 12.sp,
                            color    = Color(0xFF888888)
                        )
                    }
                }
            }

            is ChatMessage.VideoMsg -> {
                Box(
                    modifier = Modifier
                        .size(200.dp, 150.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model              = message.thumbnailUrl,
                        contentDescription = "Video",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                    // Dark overlay
                    Box(
                        modifier         = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint               = Color(0xFF1A1A1A),
                                modifier           = Modifier.size(28.dp)
                            )
                        }
                    }
                    // Duration badge
                    Text(
                        text     = message.duration,
                        fontSize = 11.sp,
                        color    = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                    )
                }
            }

            is ChatMessage.SharedPost -> {
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F8F8))
                ) {
                    Column {
                        Row(
                            modifier          = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = message.postUser.first().uppercaseChar().toString(),
                                    color      = Primary,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text       = message.postUser,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color(0xFF1A1A1A)
                            )
                        }
                        AsyncImage(
                            model              = message.imageUrl,
                            contentDescription = "Shared post",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                        Text(
                            text     = message.postCaption,
                            fontSize = 12.sp,
                            color    = Color(0xFF888888),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            is ChatMessage.LikeMsg -> {
                Icon(
                    imageVector        = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint               = Color(0xFFEF4444),
                    modifier           = Modifier.size(32.dp)
                )
            }
        }
    }

    // Timestamp — shown below each message
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text     = message.timeStamp,
            fontSize = 10.sp,
            color    = Color(0xFFCCCCCC),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// ── Chat Input Bar ────────────────────────────

@Composable
fun ChatInputBar(
    text         : String,
    onTextChange : (String) -> Unit,
    onSend       : () -> Unit
) {
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Camera icon
        IconButton(onClick = { }) {
            Icon(
                imageVector        = Icons.Outlined.CameraAlt,
                contentDescription = "Camera",
                tint               = Color(0xFF1A1A1A),
                modifier           = Modifier.size(24.dp)
            )
        }

        // Text input
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (text.isEmpty()) {
                Text("Message...", fontSize = 14.sp, color = Color(0xFFAAAAAA))
            }
            BasicTextField(
                value         = text,
                onValueChange = onTextChange,
                textStyle     = TextStyle(fontSize = 14.sp, color = Color(0xFF1A1A1A)),
                modifier      = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.width(8.dp))

        if (text.isEmpty()) {
            // Media icons when no text
            Row {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector        = Icons.Outlined.Image,
                        contentDescription = "Gallery",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector        = Icons.Outlined.Mic,
                        contentDescription = "Audio",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // Send button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable(
                        indication        = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Send,
                    contentDescription = "Send",
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    ChatContent()
}