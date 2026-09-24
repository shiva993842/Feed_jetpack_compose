package com.example.feed.View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.feed.Model.PostModel
import com.example.feed.R
import com.example.feed.ui.theme.Primary

@Composable
fun PostCard(
    post           : PostModel,
    currentUserId  : String     = "",
    onSaveClick    : () -> Unit = {},
    // Called when the user taps an avatar/username — caller provides userId + username
    onProfileClick : (userId: String, username: String) -> Unit = { _, _ -> }
) {
    var isLiked      by remember { mutableStateOf(post.likes.contains(currentUserId)) }
    var likeCount    by remember { mutableStateOf(post.likes.size) }
    var isSaved      by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var showLikes    by remember { mutableStateOf(false) }
    var showShare    by remember { mutableStateOf(false) }

    val shareText = buildString {
        if (post.caption.isNotEmpty())  append("${post.caption}\n")
        if (post.imageUrl.isNotEmpty()) append(post.imageUrl)
        else                            append("Check out this post by @${post.username}!")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        // ── Post Header ────────────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tapping the avatar / username navigates to FriendsProfileScreen
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.clickable {
                    onProfileClick(post.userId, post.username)
                }
            ) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (post.userAvatar.isNotEmpty()) {
                        AsyncImage(
                            model              = post.userAvatar,
                            contentDescription = "Avatar",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text       = post.username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            color      = Primary,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text       = post.username,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1A1A1A)
                    )
                    if (post.location.isNotEmpty()) {
                        Text(text = post.location, fontSize = 11.sp, color = Color(0xFF888888))
                    }
                }
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF1A1A1A))
            }
        }

        // ── Post Image ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFF0F0F0))
        ) {
            if (post.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model              = post.imageUrl,
                    contentDescription = "Post Image",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier         = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(60.dp))
                }
            }
        }

        // ── Action Row ─────────────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Like
                IconButton(onClick = {
                    isLiked   = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                }) {
                    Icon(
                        imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint               = if (isLiked) Color(0xFFEF4444) else Color(0xFF1A1A1A),
                        modifier           = Modifier.size(26.dp)
                    )
                }
                if (likeCount > 0) {
                    Text(
                        text       = "$likeCount",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF1A1A1A),
                        modifier   = Modifier.clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showLikes = true }
                    )
                }
                Spacer(Modifier.width(4.dp))
                // Comment
                IconButton(onClick = { showComments = true }) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF1A1A1A), modifier = Modifier.size(24.dp))
                }
                // Share
                IconButton(onClick = { showShare = true }) {
                    Icon(
                        painter            = painterResource(id = R.drawable.share),
                        contentDescription = "Share",
                        tint               = Color(0xFF1A1A1A),
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
            // Save
            IconButton(onClick = {
                isSaved = !isSaved
                onSaveClick()
            }) {
                Icon(
                    imageVector        = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Save",
                    tint               = Color(0xFF1A1A1A),
                    modifier           = Modifier.size(24.dp)
                )
            }
        }

        // ── Caption ────────────────────────────────────────────────────────
        if (post.caption.isNotEmpty()) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                Text(
                    text     = post.username,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A),
                    modifier = Modifier.clickable { onProfileClick(post.userId, post.username) }
                )
                Spacer(Modifier.width(4.dp))
                Text(post.caption, fontSize = 14.sp, color = Color(0xFF1A1A1A), maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }

        // ── View all comments ──────────────────────────────────────────────
        if (post.commentsCount > 0) {
            Text(
                text     = "View all ${post.commentsCount} comments",
                fontSize = 14.sp,
                color    = Color(0xFF888888),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { showComments = true }
            )
        }

        // ── Timestamp ──────────────────────────────────────────────────────
        Text(
            text     = getTimeAgo(post.timestamp),
            fontSize = 11.sp,
            color    = Color(0xFFAAAAAA),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }

    // ── Bottom Sheets ──────────────────────────────────────────────────────
    if (showComments) { CommentsBottomSheet(onDismiss = { showComments = false }) }
    if (showLikes)    { LikesBottomSheet(likesCount = likeCount, onDismiss = { showLikes = false }) }
    if (showShare)    { ShareBottomSheet(shareText = shareText, onDismiss = { showShare = false }) }
}

fun getTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val diff    = System.currentTimeMillis() - timestamp
    val minutes = diff / (60 * 1000)
    val hours   = diff / (60 * 60 * 1000)
    val days    = diff / (24 * 60 * 60 * 1000)
    return when {
        minutes < 1  -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours   < 24 -> "${hours}h ago"
        days    < 7  -> "${days}d ago"
        else         -> "${days / 7}w ago"
    }
}

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    PostCard(
        post = PostModel(
            postId        = "1",
            userId        = "user1",
            username      = "ronaldo_23",
            userAvatar    = "",
            imageUrl      = "",
            caption       = "Beautiful day! 🌟 Loving every moment of this journey",
            location      = "Hyderabad, India",
            likes         = listOf("user1", "user2", "user3"),
            commentsCount = 12,
            timestamp     = System.currentTimeMillis() - 3600000
        )
    )
}