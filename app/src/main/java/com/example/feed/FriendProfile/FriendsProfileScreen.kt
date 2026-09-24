package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.feed.MyProfile.ProfilePostItem
import com.example.feed.MyProfile.StoryHighlight
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary

// ── Static friend profile data ─────────────────────────────────────────────

val friendPosts = listOf(
    ProfilePostItem("fp1",  "https://picsum.photos/seed/fr1/400/400",  isReel = false, likes = 234,  comments = 18),
    ProfilePostItem("fp2",  "https://picsum.photos/seed/fr2/400/400",  isReel = true,  likes = 1540, comments = 67),
    ProfilePostItem("fp3",  "https://picsum.photos/seed/fr3/400/400",  isReel = false, likes = 89,   comments = 11),
    ProfilePostItem("fp4",  "https://picsum.photos/seed/fr4/400/400",  isReel = false, likes = 456,  comments = 34),
    ProfilePostItem("fp5",  "https://picsum.photos/seed/fr5/400/400",  isReel = true,  likes = 2300, comments = 112),
    ProfilePostItem("fp6",  "https://picsum.photos/seed/fr6/400/400",  isReel = false, likes = 73,   comments = 7),
    ProfilePostItem("fp7",  "https://picsum.photos/seed/fr7/400/400",  isReel = false, likes = 621,  comments = 45),
    ProfilePostItem("fp8",  "https://picsum.photos/seed/fr8/400/400",  isReel = true,  likes = 890,  comments = 59),
    ProfilePostItem("fp9",  "https://picsum.photos/seed/fr9/400/400",  isReel = false, likes = 147,  comments = 22),
    ProfilePostItem("fp10", "https://picsum.photos/seed/fr10/400/400", isReel = false, likes = 312,  comments = 28),
    ProfilePostItem("fp11", "https://picsum.photos/seed/fr11/400/400", isReel = false, likes = 99,   comments = 13),
    ProfilePostItem("fp12", "https://picsum.photos/seed/fr12/400/400", isReel = true,  likes = 3100, comments = 143),
)

val friendHighlights = listOf(
    StoryHighlight("Trips",   "https://picsum.photos/seed/fhl1/150/150"),
    StoryHighlight("Sunsets", "https://picsum.photos/seed/fhl2/150/150"),
    StoryHighlight("Food",    "https://picsum.photos/seed/fhl3/150/150"),
    StoryHighlight("Family",  "https://picsum.photos/seed/fhl4/150/150"),
    StoryHighlight("Work",    "https://picsum.photos/seed/fhl5/150/150"),
)

// ── Entry composable ───────────────────────────────────────────────────────

@Composable
fun FriendsProfileScreen(
    navController : NavController,
    userId        : String,
    username      : String
) {
    FriendsProfileContent(
        username       = username,
        avatarUrl      = "https://picsum.photos/seed/avatar_$userId/300/300",
        onBackClick    = { navController.popBackStack() },
        // Message button → directly opens ChatScreen for this user
        onMessageClick = {
            navController.navigate(Screen.ChatScreen.withArgs(userId, username))
        }
    )
}

// ── Content composable (preview-friendly) ─────────────────────────────────

@Composable
fun FriendsProfileContent(
    username       : String     = "ronaldo_23",
    avatarUrl      : String     = "https://picsum.photos/seed/avatar_user1/300/300",
    onBackClick    : () -> Unit = {},
    onMessageClick : () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isFollowing by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns        = GridCells.Fixed(3),
        modifier       = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item(span = { GridItemSpan(3) }) {
            FriendProfileTopBar(username = username, onBackClick = onBackClick)
        }

        item(span = { GridItemSpan(3) }) {
            FriendProfileHeader(
                avatarUrl      = avatarUrl,
                username       = username,
                isFollowing    = isFollowing,
                onFollowClick  = { isFollowing = !isFollowing },
                onMessageClick = onMessageClick
            )
        }

        item(span = { GridItemSpan(3) }) { FriendHighlightsRow() }

        item(span = { GridItemSpan(3) }) {
            FriendProfileTabRow(
                selectedTab   = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        if (selectedTab == 0) {
            items(friendPosts) { post -> FriendProfileGridItem(post = post) }
        } else {
            items(friendPosts.take(6)) { post ->
                FriendProfileGridItem(post = post.copy(imageUrl = post.imageUrl.replace("fr", "tag")))
            }
        }
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────

@Composable
fun FriendProfileTopBar(username: String, onBackClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
        }
        Text(
            text       = username,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF1A1A1A),
            modifier   = Modifier.weight(1f)
        )
        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF1A1A1A))
        }
    }
}

// ── Profile Header ─────────────────────────────────────────────────────────

@Composable
fun FriendProfileHeader(
    avatarUrl      : String,
    username       : String,
    isFollowing    : Boolean,
    onFollowClick  : () -> Unit = {},
    onMessageClick : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Avatar + stats
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier.size(86.dp).clip(CircleShape)
                    .border(2.5.dp, Primary, CircleShape).padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = avatarUrl, contentDescription = username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }
            Spacer(Modifier.width(24.dp))
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                FriendStatItem("248",   "Posts")
                FriendStatItem("12.4K", "Followers")
                FriendStatItem("389",   "Following")
            }
        }

        Spacer(Modifier.height(10.dp))

        // Name + bio
        Text("Ronaldo Varma", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
        Spacer(Modifier.height(2.dp))
        Text(
            text       = "📸 Photography enthusiast\n🌍 Traveller | Hyderabad, India\n✨ Capturing life, one frame at a time",
            fontSize   = 12.sp,
            color      = Color(0xFF444444),
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        // ── Action buttons: Follow | Message | PersonAdd ──────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            // Follow / Following
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFollowing) Color.White else Primary)
                    .border(
                        width = if (isFollowing) 1.dp else 0.dp,
                        color = if (isFollowing) Color(0xFFDDDDDD) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onFollowClick() }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = if (isFollowing) "Following" else "Follow",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isFollowing) Color(0xFF1A1A1A) else Color.White
                )
            }

            // Message → navigates to ChatScreen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onMessageClick() }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Message", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
            }

            // Suggest friends
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { }
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PersonAdd, contentDescription = "Suggest", tint = Color(0xFF1A1A1A), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun FriendStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 16.sp, fontWeight = FontWeight.Bold,  color = Color(0xFF1A1A1A))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Color(0xFF888888))
    }
}

// ── Story Highlights ───────────────────────────────────────────────────────

@Composable
fun FriendHighlightsRow() {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        friendHighlights.forEach { h ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
                Box(modifier = Modifier.size(62.dp).clip(CircleShape).border(1.5.dp, Color(0xFFDDDDDD), CircleShape)) {
                    AsyncImage(model = h.thumbUrl, contentDescription = h.label, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(4.dp))
                Text(text = h.label, fontSize = 11.sp, color = Color(0xFF1A1A1A), maxLines = 1, textAlign = TextAlign.Center)
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

// ── Tab Row ────────────────────────────────────────────────────────────────

@Composable
fun FriendProfileTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Column {
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            FriendProfileTab(Icons.Default.GridOn,       selectedTab == 0, { onTabSelected(0) }, Modifier.weight(1f))
            FriendProfileTab(Icons.Outlined.PersonPin,   selectedTab == 1, { onTabSelected(1) }, Modifier.weight(1f))
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

@Composable
fun FriendProfileTab(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFAAAAAA), modifier = Modifier.size(22.dp))
        if (isSelected) {
            Spacer(Modifier.height(2.dp))
            Box(modifier = Modifier.width(20.dp).height(2.dp).background(Color(0xFF1A1A1A)))
        }
    }
}

// ── Grid item ──────────────────────────────────────────────────────────────

@Composable
fun FriendProfileGridItem(post: ProfilePostItem) {
    var showOverlay by remember { mutableStateOf(false) }
    Box(modifier = Modifier.aspectRatio(1f).padding(0.5.dp).clickable { showOverlay = !showOverlay }) {
        AsyncImage(model = post.imageUrl, contentDescription = "Post", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        if (post.isReel) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Reel", tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp))
        }
        if (showOverlay) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${post.likes}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FriendsProfileScreenPreview() {
    FriendsProfileContent()
}