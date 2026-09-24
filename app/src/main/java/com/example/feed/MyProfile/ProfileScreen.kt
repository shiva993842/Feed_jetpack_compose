package com.example.feed.MyProfile

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController  // ← ADDED (needed for Preview)
import coil.compose.AsyncImage
import com.example.feed.View.components.FeedBottomNavBar
import com.example.feed.navigation.Screen
import com.example.feed.ui.theme.Primary

// ── Static profile post data ──────────────────
data class ProfilePostItem(
    val id       : String,
    val imageUrl : String,
    val isReel   : Boolean = false,
    val likes    : Int     = 0,
    val comments : Int     = 0
)

val profilePosts = listOf(
    ProfilePostItem("p1",  "https://picsum.photos/seed/pp1/400/400",  isReel = false, likes = 134, comments = 12),
    ProfilePostItem("p2",  "https://picsum.photos/seed/pp2/400/400",  isReel = true,  likes = 890, comments = 43),
    ProfilePostItem("p3",  "https://picsum.photos/seed/pp3/400/400",  isReel = false, likes = 67,  comments = 8),
    ProfilePostItem("p4",  "https://picsum.photos/seed/pp4/400/400",  isReel = false, likes = 223, comments = 31),
    ProfilePostItem("p5",  "https://picsum.photos/seed/pp5/400/400",  isReel = true,  likes = 1200,comments = 77),
    ProfilePostItem("p6",  "https://picsum.photos/seed/pp6/400/400",  isReel = false, likes = 45,  comments = 5),
    ProfilePostItem("p7",  "https://picsum.photos/seed/pp7/400/400",  isReel = false, likes = 389, comments = 22),
    ProfilePostItem("p8",  "https://picsum.photos/seed/pp8/400/400",  isReel = true,  likes = 567, comments = 49),
    ProfilePostItem("p9",  "https://picsum.photos/seed/pp9/400/400",  isReel = false, likes = 112, comments = 14),
    ProfilePostItem("p10", "https://picsum.photos/seed/pp10/400/400", isReel = false, likes = 78,  comments = 9),
    ProfilePostItem("p11", "https://picsum.photos/seed/pp11/400/400", isReel = false, likes = 234, comments = 18),
    ProfilePostItem("p12", "https://picsum.photos/seed/pp12/400/400", isReel = true,  likes = 2100,comments = 93),
)

// ── Static highlight stories data ────────────
data class StoryHighlight(val label: String, val thumbUrl: String)

val profileHighlights = listOf(
    StoryHighlight("Travel",   "https://picsum.photos/seed/hl1/150/150"),
    StoryHighlight("Food",     "https://picsum.photos/seed/hl2/150/150"),
    StoryHighlight("Fitness",  "https://picsum.photos/seed/hl3/150/150"),
    StoryHighlight("Friends",  "https://picsum.photos/seed/hl4/150/150"),
    StoryHighlight("Art",      "https://picsum.photos/seed/hl5/150/150"),
)

@Composable
fun ProfileScreen(navController: NavController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val selectedRoute    = currentBackStack?.destination?.route ?: Screen.Profile.route

    ProfileContent(
        selectedRoute  = selectedRoute,
        navController  = navController,
        onNavItemClick = { route -> if (route != selectedRoute) navController.navigate(route) },
        onMenuClick    = { navController.navigate(Screen.Settings.route) }  // ← ADD THIS
    )
}

@Composable
fun ProfileContent(
    selectedRoute  : String           = "profile",
    navController  : NavController?   = null,                              // ← ADDED
    onNavItemClick : (String) -> Unit = {},
    onMenuClick    : () -> Unit       = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor      = Color.White,
        bottomBar = {
            FeedBottomNavBar(
                selectedRoute = selectedRoute,
                onItemClick   = onNavItemClick
            )
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            columns        = GridCells.Fixed(3),
            modifier       = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            item(span = { GridItemSpan(3) }) {
                ProfileHeader(
                    onEditProfileClick = {                                  // ← ADDED
                        navController?.navigate(Screen.EditProfile.route)  // ← ADDED
                    }   ,
                    onMenuClick = onMenuClick// ← ADDED
                )
            }

            item(span = { GridItemSpan(3) }) {
                HighlightsRow()
            }

            item(span = { GridItemSpan(3) }) {
                ProfileTabRow(
                    selectedTab   = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            if (selectedTab == 0) {
                items(profilePosts) { post ->
                    ProfileGridItem(post = post)
                }
            } else {
                items(profilePosts.take(6)) { post ->
                    ProfileGridItem(
                        post = post.copy(
                            imageUrl = post.imageUrl.replace("pp", "tg")
                        )
                    )
                }
            }
        }
    }
}

// ── Profile Header Section ─────────────────────────────────────────────────

@Composable
fun ProfileHeader(
    onEditProfileClick : () -> Unit = {},
    onMenuClick        : () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Username row
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "andrew_mundy",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    imageVector        = Icons.Default.Settings,
                    contentDescription = "Menu",
                    tint               = Color(0xFF1A1A1A),
                    modifier           = Modifier
                        .size(24.dp)
                        .clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onMenuClick() }               // ← ADD THIS
                )
            }
        }

        // Avatar + Stats row
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, Primary, CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model              = "https://picsum.photos/seed/profile_main/200/200",
                    contentDescription = "Profile picture",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.width(16.dp))

            Row(
                modifier              = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(count = "1,487", label = "Posts")
                ProfileStatItem(count = "898",   label = "Followers")
                ProfileStatItem(count = "1,310", label = "Following")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text       = "Andrew Mundy",
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text     = "Most of these photos are developed and scanned at home by hand. 📷 #olympusmjuii #35mm 📷",
            fontSize = 13.sp,
            color    = Color(0xFF1A1A1A)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text     = "SF, CA",
            fontSize = 13.sp,
            color    = Color(0xFF1A1A1A)
        )
        Text(
            text     = "www.andrewmundy.net",
            fontSize = 13.sp,
            color    = Color(0xFF0095F6)
        )

        Spacer(Modifier.height(12.dp))

        // Action Buttons
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Edit Profile — NOW NAVIGATES                                 // ← CHANGED
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                    .clickable(
                        indication        = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onEditProfileClick() }                             // ← CHANGED  (was empty { })
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "Edit profile",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
            }

            // Share Profile
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(8.dp))
                    .clickable(
                        indication        = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "Share profile",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Bold,  color = Color(0xFF1A1A1A))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Color(0xFF888888))
    }
}

// ── Story Highlights Row ──────────────────────

@Composable
fun HighlightsRow() {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        profileHighlights.forEach { highlight ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.width(64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFFDDDDDD), CircleShape)
                ) {
                    AsyncImage(
                        model              = highlight.thumbUrl,
                        contentDescription = highlight.label,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text      = highlight.label,
                    fontSize  = 11.sp,
                    color     = Color(0xFF1A1A1A),
                    maxLines  = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

// ── Tab Row ───────────────────────────────────

@Composable
fun ProfileTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Column {
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            ProfileTab(
                icon       = Icons.Default.GridOn,
                isSelected = selectedTab == 0,
                onClick    = { onTabSelected(0) },
                modifier   = Modifier.weight(1f)
            )
            ProfileTab(
                icon       = Icons.Outlined.PersonPin,
                isSelected = selectedTab == 1,
                onClick    = { onTabSelected(1) },
                modifier   = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

@Composable
fun ProfileTab(
    icon       : ImageVector,
    isSelected : Boolean,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFAAAAAA),
            modifier           = Modifier.size(22.dp)
        )
        if (isSelected) {
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFF1A1A1A))
            )
        }
    }
}

// ── Single grid post thumbnail ────────────────

@Composable
fun ProfileGridItem(post: ProfilePostItem) {
    var showOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.5.dp)
            .clickable { showOverlay = !showOverlay }
    ) {
        AsyncImage(
            model              = post.imageUrl,
            contentDescription = "Post",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        if (post.isReel) {
            Icon(
                imageVector        = Icons.Default.PlayArrow,
                contentDescription = "Reel",
                tint               = Color.White,
                modifier           = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(18.dp)
            )
        }

        if (showOverlay) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Default.Favorite,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(text = "${post.likes}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileContent(navController = rememberNavController())                // ← CHANGED
}