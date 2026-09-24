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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.feed.MyProfile.ProfilePostItem

// ── Saved collection model ─────────────────────────────────────────────────

data class SavedCollection(
    val id        : String,
    val name      : String,
    val coverUrls : List<String>,   // up to 4 cover thumbnails
    val postCount : Int
)

// ── Static saved data ──────────────────────────────────────────────────────

val savedAllPosts = listOf(
    ProfilePostItem("s1",  "https://picsum.photos/seed/sv1/400/400",  isReel = false, likes = 421,  comments = 33),
    ProfilePostItem("s2",  "https://picsum.photos/seed/sv2/400/400",  isReel = true,  likes = 1870, comments = 88),
    ProfilePostItem("s3",  "https://picsum.photos/seed/sv3/400/400",  isReel = false, likes = 156,  comments = 14),
    ProfilePostItem("s4",  "https://picsum.photos/seed/sv4/400/400",  isReel = false, likes = 673,  comments = 51),
    ProfilePostItem("s5",  "https://picsum.photos/seed/sv5/400/400",  isReel = true,  likes = 3100, comments = 134),
    ProfilePostItem("s6",  "https://picsum.photos/seed/sv6/400/400",  isReel = false, likes = 88,   comments = 9),
    ProfilePostItem("s7",  "https://picsum.photos/seed/sv7/400/400",  isReel = false, likes = 502,  comments = 42),
    ProfilePostItem("s8",  "https://picsum.photos/seed/sv8/400/400",  isReel = true,  likes = 1120, comments = 67),
    ProfilePostItem("s9",  "https://picsum.photos/seed/sv9/400/400",  isReel = false, likes = 234,  comments = 21),
    ProfilePostItem("s10", "https://picsum.photos/seed/sv10/400/400", isReel = false, likes = 97,   comments = 11),
    ProfilePostItem("s11", "https://picsum.photos/seed/sv11/400/400", isReel = false, likes = 389,  comments = 29),
    ProfilePostItem("s12", "https://picsum.photos/seed/sv12/400/400", isReel = true,  likes = 4500, comments = 198),
    ProfilePostItem("s13", "https://picsum.photos/seed/sv13/400/400", isReel = false, likes = 178,  comments = 16),
    ProfilePostItem("s14", "https://picsum.photos/seed/sv14/400/400", isReel = false, likes = 563,  comments = 47),
    ProfilePostItem("s15", "https://picsum.photos/seed/sv15/400/400", isReel = true,  likes = 2780, comments = 103),
    ProfilePostItem("s16", "https://picsum.photos/seed/sv16/400/400", isReel = false, likes = 312,  comments = 26),
    ProfilePostItem("s17", "https://picsum.photos/seed/sv17/400/400", isReel = false, likes = 741,  comments = 58),
    ProfilePostItem("s18", "https://picsum.photos/seed/sv18/400/400", isReel = true,  likes = 1430, comments = 79),
)

val savedCollections = listOf(
    SavedCollection(
        id        = "c1",
        name      = "Travel",
        coverUrls = listOf(
            "https://picsum.photos/seed/sv1/400/400",
            "https://picsum.photos/seed/sv2/400/400",
            "https://picsum.photos/seed/sv3/400/400",
            "https://picsum.photos/seed/sv4/400/400",
        ),
        postCount = 24
    ),
    SavedCollection(
        id        = "c2",
        name      = "Food",
        coverUrls = listOf(
            "https://picsum.photos/seed/sv5/400/400",
            "https://picsum.photos/seed/sv6/400/400",
            "https://picsum.photos/seed/sv7/400/400",
            "https://picsum.photos/seed/sv8/400/400",
        ),
        postCount = 17
    ),
    SavedCollection(
        id        = "c3",
        name      = "Fitness",
        coverUrls = listOf(
            "https://picsum.photos/seed/sv9/400/400",
            "https://picsum.photos/seed/sv10/400/400",
            "https://picsum.photos/seed/sv11/400/400",
            "https://picsum.photos/seed/sv12/400/400",
        ),
        postCount = 11
    ),
    SavedCollection(
        id        = "c4",
        name      = "Inspiration",
        coverUrls = listOf(
            "https://picsum.photos/seed/sv13/400/400",
            "https://picsum.photos/seed/sv14/400/400",
            "https://picsum.photos/seed/sv15/400/400",
            "https://picsum.photos/seed/sv16/400/400",
        ),
        postCount = 38
    ),
)

// ── Entry composable ───────────────────────────────────────────────────────

@Composable
fun SavedScreen(navController: NavController) {
    SavedContent(
        onBackClick = { navController.popBackStack() }
    )
}

// ── Content composable (preview-friendly) ─────────────────────────────────

@Composable
fun SavedContent(
    onBackClick : () -> Unit = {}
) {
    // 0 = All Posts grid, 1 = Collections
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor      = Color.White,
        topBar = {
            SavedTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            columns        = GridCells.Fixed(3),
            modifier       = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ── Tab Row ───────────────────────────────
            item(span = { GridItemSpan(3) }) {
                SavedTabRow(
                    selectedTab   = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            if (selectedTab == 0) {
                // ── All saved posts grid ──────────────
                items(savedAllPosts) { post ->
                    SavedGridItem(post = post)
                }
            } else {
                // ── Collections (2-col) ───────────────
                // Use span=3 but render pairs manually for 2-column layout
                val chunked = savedCollections.chunked(2)
                items(chunked, span = { GridItemSpan(3) }) { pair ->
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { collection ->
                            SavedCollectionCard(
                                collection = collection,
                                modifier   = Modifier.weight(1f)
                            )
                        }
                        // fill empty slot if odd count
                        if (pair.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }

                // ── Create new collection button ──────
                item(span = { GridItemSpan(3) }) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(10.dp))
                                .clickable(
                                    indication        = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Add,
                                contentDescription = "New collection",
                                tint               = Color(0xFF1A1A1A),
                                modifier           = Modifier.size(18.dp)
                            )
                            Text(
                                text       = "New collection",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────

@Composable
fun SavedTopBar(onBackClick: () -> Unit = {}) {
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
            Text(
                text       = "Saved",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A1A),
                modifier   = Modifier.weight(1f)
            )
            // Bookmark icon as visual accent
            Icon(
                imageVector        = Icons.Default.Bookmark,
                contentDescription = null,
                tint               = Color(0xFF1A1A1A),
                modifier           = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
            )
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

// ── Tab Row ────────────────────────────────────────────────────────────────

@Composable
fun SavedTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            SavedTab(
                icon       = Icons.Default.GridOn,
                label      = "Posts",
                isSelected = selectedTab == 0,
                onClick    = { onTabSelected(0) },
                modifier   = Modifier.weight(1f)
            )
            SavedTab(
                icon       = Icons.Outlined.BookmarkBorder,
                label      = "Collections",
                isSelected = selectedTab == 1,
                onClick    = { onTabSelected(1) },
                modifier   = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

@Composable
fun SavedTab(
    icon       : androidx.compose.ui.graphics.vector.ImageVector,
    label      : String,
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
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFAAAAAA),
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (isSelected) Color(0xFF1A1A1A) else Color(0xFFAAAAAA)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.dp)
                    .background(Color(0xFF1A1A1A))
            )
        }
    }
}

// ── Saved Post Grid Item ───────────────────────────────────────────────────

@Composable
fun SavedGridItem(post: ProfilePostItem) {
    var showOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.5.dp)
            .clickable { showOverlay = !showOverlay }
    ) {
        AsyncImage(
            model              = post.imageUrl,
            contentDescription = "Saved post",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // Saved bookmark badge (top-right)
        Icon(
            imageVector        = Icons.Default.Bookmark,
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier
                .align(Alignment.TopEnd)
                .padding(5.dp)
                .size(16.dp)
        )

        if (post.isReel) {
            Icon(
                imageVector        = Icons.Default.PlayArrow,
                contentDescription = "Reel",
                tint               = Color.White,
                modifier           = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .size(16.dp)
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
                        Text(
                            text       = "${post.likes}",
                            color      = Color.White,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Collection Card ────────────────────────────────────────────────────────

@Composable
fun SavedCollectionCard(
    collection : SavedCollection,
    modifier   : Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { }
    ) {
        // 2×2 grid of cover images
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF0F0F0))
        ) {
            if (collection.coverUrls.isNotEmpty()) {
                // show 2x2 thumbnails
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model              = collection.coverUrls.getOrElse(0) { "" },
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.weight(1f).fillMaxHeight()
                        )
                        Spacer(Modifier.width(1.5.dp))
                        AsyncImage(
                            model              = collection.coverUrls.getOrElse(1) { "" },
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                    Spacer(Modifier.height(1.5.dp))
                    Row(modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model              = collection.coverUrls.getOrElse(2) { "" },
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.weight(1f).fillMaxHeight()
                        )
                        Spacer(Modifier.width(1.5.dp))
                        AsyncImage(
                            model              = collection.coverUrls.getOrElse(3) { "" },
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text       = collection.name,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF1A1A1A)
        )
        Text(
            text     = "${collection.postCount} posts",
            fontSize = 12.sp,
            color    = Color(0xFF888888)
        )
        Spacer(Modifier.height(12.dp))
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SavedScreenPreview() {
    SavedContent()
}