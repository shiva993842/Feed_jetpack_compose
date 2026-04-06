package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.feed.ui.theme.Primary

// ── Conversation data model ───────────────────
data class ConversationModel(
    val userId        : String,
    val username      : String,
    val avatarUrl     : String,
    val lastMessage   : String,
    val timeAgo       : String,
    val unreadCount   : Int     = 0,
    val isOnline      : Boolean = false,
    val isStory       : Boolean = false   // has unseen story
)

val dummyConversations = listOf(
    ConversationModel("u1",  "ronaldo_23",       "https://picsum.photos/seed/avatar1/150/150",  "Haha that's crazy 😂",               "2m",   unreadCount = 3, isOnline = true,  isStory = true),
    ConversationModel("u2",  "shiva_photos",     "https://picsum.photos/seed/avatar2/150/150",  "Sent you a photo 📸",                "15m",  unreadCount = 1, isOnline = false, isStory = true),
    ConversationModel("u3",  "travel_vibes",     "https://picsum.photos/seed/avatar3/150/150",  "Where are you right now?",           "1h",   unreadCount = 0, isOnline = true,  isStory = false),
    ConversationModel("u4",  "food_lover",       "https://picsum.photos/seed/avatar4/150/150",  "Try that new biryani place 🍛",      "3h",   unreadCount = 0, isOnline = false, isStory = true),
    ConversationModel("u5",  "city_life",        "https://picsum.photos/seed/avatar5/150/150",  "Liked your photo ❤️",               "5h",   unreadCount = 0, isOnline = true,  isStory = false),
    ConversationModel("u6",  "marklavern",       "https://picsum.photos/seed/avatar6/150/150",  "You: See you tomorrow!",             "1d",   unreadCount = 0, isOnline = false, isStory = false),
    ConversationModel("u7",  "amberberry",       "https://picsum.photos/seed/avatar7/150/150",  "Sent a reel 🎬",                     "2d",   unreadCount = 0, isOnline = false, isStory = false),
    ConversationModel("u8",  "fitness_arjun",    "https://picsum.photos/seed/avatar8/150/150",  "You: Great workout bro! 💪",          "3d",   unreadCount = 0, isOnline = false, isStory = false),
    ConversationModel("u9",  "nature_explorer",  "https://picsum.photos/seed/avatar9/150/150",  "Check out this trail 🌿",            "4d",   unreadCount = 0, isOnline = false, isStory = false),
    ConversationModel("u10", "art_by_meera",     "https://picsum.photos/seed/avatar10/150/150", "Your art is amazing! 🎨",            "1w",   unreadCount = 0, isOnline = false, isStory = false),
)

@Composable
fun MessagesScreen(navController: NavController) {
    MessagesContent(
        onBackClick = { navController.popBackStack() },
        onConversationClick = { userId, username ->
            navController.navigate("chat/$userId/$username")
        }
    )
}

@Composable
fun MessagesContent(
    onBackClick         : () -> Unit                 = {},
    onConversationClick : (String, String) -> Unit   = { _, _ -> }
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor      = Color.White,
        topBar = {
            MessagesTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = paddingValues
        ) {
            // ── Active Now row ────────────────
            item {
                ActiveNowRow(
                    onUserClick = { userId, username ->
                        onConversationClick(userId, username)
                    }
                )
            }

            item {
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
                Spacer(Modifier.height(4.dp))
            }

            // ── Conversation list ─────────────
            items(dummyConversations) { convo ->
                ConversationItem(
                    convo   = convo,
                    onClick = { onConversationClick(convo.userId, convo.username) }
                )
            }
        }
    }
}

// ── Messages Top Bar ──────────────────────────

@Composable
fun MessagesTopBar(onBackClick: () -> Unit = {}) {
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
                .padding(horizontal = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(24.dp)
                    )
                }
                Text(
                    text       = "andrew_mundy",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF1A1A1A)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector        = Icons.Outlined.Edit,
                    contentDescription = "New message",
                    tint               = Color(0xFF1A1A1A),
                    modifier           = Modifier.size(24.dp)
                )
            }
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

// ── Active Now horizontal row ─────────────────

@Composable
fun ActiveNowRow(onUserClick: (String, String) -> Unit = { _, _ -> }) {
    val activeUsers = dummyConversations.filter { it.isOnline }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text     = "Active now",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color    = Color(0xFF888888),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            activeUsers.forEach { user ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier
                        .width(60.dp)
                        .clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onUserClick(user.userId, user.username) }
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model              = user.avatarUrl,
                                contentDescription = user.username,
                                contentScale       = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                        }
                        // Online dot
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF44D87A))
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = user.username.take(10),
                        fontSize = 11.sp,
                        color    = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ── Single conversation row ───────────────────

@Composable
fun ConversationItem(convo: ConversationModel, onClick: () -> Unit = {}) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with optional story ring + online dot
        Box {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .then(
                        if (convo.isStory) Modifier
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFF8A00), Color(0xFFE52E71), Color(0xFF9B59B6)
                                    )
                                )
                            )
                            .padding(2.5.dp)
                        else Modifier
                    )
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model              = convo.avatarUrl,
                    contentDescription = convo.username,
                    contentScale       = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            }
            if (convo.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF44D87A))
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        // Name + last message
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = convo.username,
                fontSize   = 14.sp,
                fontWeight = if (convo.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text       = convo.lastMessage,
                fontSize   = 13.sp,
                color      = if (convo.unreadCount > 0) Color(0xFF1A1A1A) else Color(0xFF888888),
                fontWeight = if (convo.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // Time + unread badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text     = convo.timeAgo,
                fontSize = 11.sp,
                color    = Color(0xFFAAAAAA)
            )
            if (convo.unreadCount > 0) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier         = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "${convo.unreadCount}",
                        fontSize  = 10.sp,
                        color     = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MessagesScreenPreview() {
    MessagesContent()
}