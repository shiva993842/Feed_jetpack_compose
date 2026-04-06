package com.example.feed.View

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feed.R
import com.example.feed.View.components.CommentsBottomSheet
import com.example.feed.View.components.FeedBottomNavBar
import com.example.feed.View.components.LikesBottomSheet
import com.example.feed.View.components.ShareBottomSheet
import com.example.feed.navigation.Screen

// ── Data Model ─────────────────────────────────────────────────────────────

data class ReelModel(
    val reelId        : String,
    val userId        : String,
    val username      : String,
    val avatarUrl     : String,
    val videoUrl      : String,
    val thumbnailUrl  : String,
    val caption       : String,
    val songName      : String,
    val likes         : Int,
    val comments      : Int,
    val shares        : Int,
    val isFollowing   : Boolean  = false,
    val isLiked       : Boolean  = false,
    val isSaved       : Boolean  = false,
    val isVerified    : Boolean  = false
)

val dummyReels = listOf(
    ReelModel(
        reelId       = "r1",
        userId       = "user1",
        username     = "nature_explorer",
        avatarUrl    = "https://picsum.photos/seed/avatar1/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel1/450/800",
        caption      = "Lost in the wilderness 🌿 Nature always finds a way #nature #explore #travel",
        songName     = "Forest Sounds - Original Audio",
        likes        = 48200,
        comments     = 312,
        shares       = 1400,
        isVerified   = true
    ),
    ReelModel(
        reelId       = "r2",
        userId       = "user2",
        username     = "city_vibes_hyd",
        avatarUrl    = "https://picsum.photos/seed/avatar2/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel2/450/800",
        caption      = "Hyderabad nights hit different ✨🌃 #hyderabad #citylife #nightvibes",
        songName     = "City Lights - Trending",
        likes        = 92100,
        comments     = 874,
        shares       = 3200,
        isLiked      = true
    ),
    ReelModel(
        reelId       = "r3",
        userId       = "user3",
        username     = "food_with_priya",
        avatarUrl    = "https://picsum.photos/seed/avatar3/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel3/450/800",
        caption      = "Biryani recipe that'll change your life 🍛🔥 Save this! #food #recipe #biryani",
        songName     = "Dum Pukht Mix - Original",
        likes        = 215000,
        comments     = 4320,
        shares       = 18700,
        isFollowing  = true,
        isVerified   = true
    ),
    ReelModel(
        reelId       = "r4",
        userId       = "user4",
        username     = "fitness_arjun",
        avatarUrl    = "https://picsum.photos/seed/avatar4/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel4/450/800",
        caption      = "Morning workout routine 💪 No excuses, only results! #fitness #gym #motivation",
        songName     = "Power Hour - DJ Mix",
        likes        = 33700,
        comments     = 541,
        shares       = 2100
    ),
    ReelModel(
        reelId       = "r5",
        userId       = "user5",
        username     = "travel_diaries_in",
        avatarUrl    = "https://picsum.photos/seed/avatar5/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel5/450/800",
        caption      = "Manali in December is a different world 🏔️❄️ #manali #snow #travel #himachal",
        songName     = "Mountains - Aesthetic Vibes",
        likes        = 178000,
        comments     = 2910,
        shares       = 9800,
        isFollowing  = true,
        isLiked      = true
    ),
    ReelModel(
        reelId       = "r6",
        userId       = "user6",
        username     = "art_by_meera",
        avatarUrl    = "https://picsum.photos/seed/avatar6/150/150",
        videoUrl     = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        thumbnailUrl = "https://picsum.photos/seed/reel6/450/800",
        caption      = "Painted this in 3 hours 🎨✨ What do you think? #art #painting #creative",
        songName     = "Lo-fi Chill - Study Beats",
        likes        = 67400,
        comments     = 1230,
        shares       = 4500,
        isVerified   = true
    ),
)

fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "%.1fM".format(n / 1_000_000.0)
    n >= 1_000     -> "%.1fK".format(n / 1_000.0)
    else           -> n.toString()
}

@Composable
fun ReelsScreen(navController: NavController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val selectedRoute    = currentBackStack?.destination?.route ?: Screen.Reels.route

    ReelsContent(
        reels          = dummyReels,
        selectedRoute  = selectedRoute,
        onNavItemClick = { route -> if (route != selectedRoute) navController.navigate(route) },
        onBackClick    = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsContent(
    reels          : List<ReelModel>  = dummyReels,
    selectedRoute  : String           = "reels",
    onNavItemClick : (String) -> Unit = {},
    onBackClick    : () -> Unit       = {}
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Full-screen vertical pager ─────────
        VerticalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            ReelItem(
                reel      = reel,
                isVisible = pagerState.currentPage == page
            )
        }

        // ── Top bar — TRANSPARENT background, text/icons in white ───
        ReelsTopBar(modifier = Modifier.align(Alignment.TopCenter))

        // ── Bottom nav (translucent) ───────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
        ) {
            FeedBottomNavBar(
                selectedRoute = selectedRoute,
                onItemClick   = onNavItemClick
            )
        }
    }
}

// ── Single Reel Item ───────────────────────────────────────────────────────

@Composable
fun ReelItem(
    reel      : ReelModel,
    isVisible : Boolean = false
) {
    val context     = LocalContext.current
    var isLiked     by remember { mutableStateOf(reel.isLiked) }
    var likeCount   by remember { mutableStateOf(reel.likes) }
    var isSaved     by remember { mutableStateOf(reel.isSaved) }
    var isFollowing by remember { mutableStateOf(reel.isFollowing) }
    var isMuted     by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var showLikes    by remember { mutableStateOf(false) }
    var showShare    by remember { mutableStateOf(false) }

    // Bouncing music note animation
    val infiniteTransition = rememberInfiniteTransition(label = "music")
    val musicOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "musicBounce"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Thumbnail / Video ──────────────────
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(reel.thumbnailUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Reel thumbnail",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // ── Gradient overlay ───────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // ── Right side action buttons ──────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ✅ UPDATED: Avatar + Follow functionality from Phase 1
            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    AsyncImage(
                        model              = reel.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                }

                // Overlapping Follow Button
                Box(
                    modifier = Modifier
                        .offset(y = 10.dp) // Pushes it slightly below the avatar
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isFollowing) Color.White else Color(0xFF0095F6))
                        .border(
                            width = 1.dp,
                            color = if (isFollowing) Color.LightGray else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { isFollowing = !isFollowing },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = if (isFollowing) Color(0xFF0095F6) else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp)) // Extra space to account for the offset button

            // Like
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick  = {
                        isLiked   = !isLiked
                        likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint               = if (isLiked) Color(0xFFEF4444) else Color.White,
                        modifier           = Modifier.size(28.dp)
                    )
                }
                Text(
                    text       = formatCount(likeCount),
                    color      = Color.White,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.clickable { showLikes = true }
                )
            }

            // Comment
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick  = { showComments = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint               = Color.White,
                        modifier           = Modifier.size(26.dp)
                    )
                }
                Text(
                    text       = formatCount(reel.comments),
                    color      = Color.White,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Share
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick  = { showShare = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter            = painterResource(id = R.drawable.share),
                        contentDescription = "Share",
                        tint               = Color.White,
                        modifier           = Modifier.size(28.dp)
                    )
                }
                Text(
                    text       = formatCount(reel.shares),
                    color      = Color.White,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Save
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector        = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Save",
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp).clickable { isSaved = !isSaved }
                )
            }

            // More
            Icon(
                imageVector        = Icons.Default.MoreVert,
                contentDescription = "More",
                tint               = Color.White,
                modifier           = Modifier.size(24.dp).clickable { }
            )
        }

        // ── Bottom info bar ────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, end = 72.dp, bottom = 80.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text       = reel.username,
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (reel.isVerified) {
                    Icon(
                        imageVector        = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint               = Color(0xFF0095F6),
                        modifier           = Modifier.size(14.dp)
                    )
                }
                // Optional: The textual "Follow" button next to name
                if (!isFollowing) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                            .clickable { isFollowing = true }
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text("Follow", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text     = reel.caption,
                color    = Color.White,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier
                        .size(14.dp)
                        .offset(y = musicOffset.dp)
                )
                Text(
                    text     = reel.songName,
                    color    = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Mute button ────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 70.dp, end = 14.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { isMuted = !isMuted },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = "Mute",
                tint               = Color.White,
                modifier           = Modifier.size(18.dp)
            )
        }
    }

    if (showComments) {
        CommentsBottomSheet(onDismiss = { showComments = false })
    }
    if (showLikes) {
        LikesBottomSheet(likesCount = likeCount, onDismiss = { showLikes = false })
    }
    if (showShare) {
        ShareBottomSheet(
            shareText = "Check out this reel by @${reel.username}!",
            onDismiss = { showShare = false }
        )
    }
}

// ── Top Bar — NO background color, overlays the video ─────────────────────
@Composable
fun ReelsTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = "Reels",
            color      = Color.White,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector        = Icons.Outlined.CameraAlt,
            contentDescription = "Camera",
            tint               = Color.White,
            modifier           = Modifier.size(26.dp).clickable { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReelsScreenPreview() {
    ReelsContent(reels = dummyReels)
}