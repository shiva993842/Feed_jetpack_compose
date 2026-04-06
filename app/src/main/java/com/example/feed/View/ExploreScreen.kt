package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.feed.R
import com.example.feed.View.components.FeedBottomNavBar
import com.example.feed.navigation.Screen

// ── Explore item model ─────────────────────────
data class ExploreItem(
    val id          : String,
    val imageUrl    : String,
    val isReel      : Boolean = false,
    val aspectRatio : Float   = 1f   // width / height  →  portrait < 1 | square = 1 | landscape > 1
)

// ── Greedy shortest-column-first distributor ───
// For each item, pick the column with the smallest accumulated height so far.
// This guarantees all 3 columns end at nearly the same position → NO white gap at bottom.
// heightUnit = 1 / aspectRatio  (a portrait 0.5 image is 2× as tall as a square)
fun distributeToColumns(
    items      : List<ExploreItem>,
    numColumns : Int = 3
): List<List<ExploreItem>> {
    val columns     = List(numColumns) { mutableListOf<ExploreItem>() }
    val heights     = FloatArray(numColumns) { 0f }          // accumulated height units per column
    val gapUnit     = 0.01f                                  // small gap between items (2dp ≈ fraction)

    for (item in items) {
        val shortestCol = heights.indices.minByOrNull { heights[it] } ?: 0
        columns[shortestCol].add(item)
        heights[shortestCol] += (1f / item.aspectRatio) + gapUnit  // taller image = larger height unit
    }
    return columns
}

@Composable
fun ExploreScreen(navController: NavController) {
    val currentRoute  by navController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route ?: Screen.Explore.route

    ExploreContent(
        selectedRoute  = selectedRoute,
        onNavItemClick = { route ->
            if (route != selectedRoute) navController.navigate(route)
        }
    )
}

@Composable
fun ExploreContent(
    selectedRoute  : String           = "explore",
    onNavItemClick : (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val allItems = remember {
        listOf(
            // ── Batch 1 ───────────────────────────────────────────────────────
            ExploreItem("e1",  "https://picsum.photos/seed/501/400/500",  isReel = false, aspectRatio = 0.80f),
            ExploreItem("e2",  "https://picsum.photos/seed/502/400/600",  isReel = true,  aspectRatio = 0.67f),
            ExploreItem("e3",  "https://picsum.photos/seed/503/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e4",  "https://picsum.photos/seed/504/400/300",  isReel = false, aspectRatio = 1.33f),
            ExploreItem("e5",  "https://picsum.photos/seed/505/400/500",  isReel = true,  aspectRatio = 0.80f),
            ExploreItem("e6",  "https://picsum.photos/seed/506/400/620",  isReel = false, aspectRatio = 0.65f),
            ExploreItem("e7",  "https://picsum.photos/seed/507/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e8",  "https://picsum.photos/seed/508/400/550",  isReel = true,  aspectRatio = 0.73f),
            ExploreItem("e9",  "https://picsum.photos/seed/509/400/350",  isReel = false, aspectRatio = 1.14f),
            ExploreItem("e10", "https://picsum.photos/seed/510/400/500",  isReel = false, aspectRatio = 0.80f),
            ExploreItem("e11", "https://picsum.photos/seed/511/400/650",  isReel = true,  aspectRatio = 0.62f),
            ExploreItem("e12", "https://picsum.photos/seed/512/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e13", "https://picsum.photos/seed/513/400/450",  isReel = false, aspectRatio = 0.89f),
            ExploreItem("e14", "https://picsum.photos/seed/514/400/300",  isReel = true,  aspectRatio = 1.33f),
            ExploreItem("e15", "https://picsum.photos/seed/515/400/520",  isReel = false, aspectRatio = 0.77f),
            ExploreItem("e16", "https://picsum.photos/seed/516/400/600",  isReel = false, aspectRatio = 0.67f),
            ExploreItem("e17", "https://picsum.photos/seed/517/400/400",  isReel = true,  aspectRatio = 1.00f),
            ExploreItem("e18", "https://picsum.photos/seed/518/400/350",  isReel = false, aspectRatio = 1.14f),
            ExploreItem("e19", "https://picsum.photos/seed/519/400/480",  isReel = false, aspectRatio = 0.83f),
            ExploreItem("e20", "https://picsum.photos/seed/520/400/600",  isReel = true,  aspectRatio = 0.67f),
            ExploreItem("e21", "https://picsum.photos/seed/521/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e22", "https://picsum.photos/seed/522/400/460",  isReel = false, aspectRatio = 0.87f),
            ExploreItem("e23", "https://picsum.photos/seed/523/400/540",  isReel = true,  aspectRatio = 0.74f),
            ExploreItem("e24", "https://picsum.photos/seed/524/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e25", "https://picsum.photos/seed/525/400/600",  isReel = false, aspectRatio = 0.67f),
            ExploreItem("e26", "https://picsum.photos/seed/526/400/300",  isReel = true,  aspectRatio = 1.33f),
            ExploreItem("e27", "https://picsum.photos/seed/527/400/500",  isReel = false, aspectRatio = 0.80f),
            ExploreItem("e28", "https://picsum.photos/seed/528/400/640",  isReel = true,  aspectRatio = 0.63f),
            ExploreItem("e29", "https://picsum.photos/seed/529/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e30", "https://picsum.photos/seed/530/400/360",  isReel = false, aspectRatio = 1.11f),
            // ── Batch 2 ───────────────────────────────────────────────────────
            ExploreItem("e31", "https://picsum.photos/seed/601/400/560",  isReel = false, aspectRatio = 0.71f),
            ExploreItem("e32", "https://picsum.photos/seed/602/400/400",  isReel = true,  aspectRatio = 1.00f),
            ExploreItem("e33", "https://picsum.photos/seed/603/400/280",  isReel = false, aspectRatio = 1.43f),
            ExploreItem("e34", "https://picsum.photos/seed/604/400/530",  isReel = true,  aspectRatio = 0.75f),
            ExploreItem("e35", "https://picsum.photos/seed/605/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e36", "https://picsum.photos/seed/606/400/660",  isReel = false, aspectRatio = 0.61f),
            ExploreItem("e37", "https://picsum.photos/seed/607/400/320",  isReel = true,  aspectRatio = 1.25f),
            ExploreItem("e38", "https://picsum.photos/seed/608/400/490",  isReel = false, aspectRatio = 0.82f),
            ExploreItem("e39", "https://picsum.photos/seed/609/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e40", "https://picsum.photos/seed/610/400/580",  isReel = true,  aspectRatio = 0.69f),
            ExploreItem("e41", "https://picsum.photos/seed/611/400/340",  isReel = false, aspectRatio = 1.18f),
            ExploreItem("e42", "https://picsum.photos/seed/612/400/510",  isReel = true,  aspectRatio = 0.78f),
            ExploreItem("e43", "https://picsum.photos/seed/613/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e44", "https://picsum.photos/seed/614/400/620",  isReel = false, aspectRatio = 0.65f),
            ExploreItem("e45", "https://picsum.photos/seed/615/400/300",  isReel = true,  aspectRatio = 1.33f),
            ExploreItem("e46", "https://picsum.photos/seed/616/400/470",  isReel = false, aspectRatio = 0.85f),
            ExploreItem("e47", "https://picsum.photos/seed/617/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e48", "https://picsum.photos/seed/618/400/590",  isReel = true,  aspectRatio = 0.68f),
            ExploreItem("e49", "https://picsum.photos/seed/619/400/330",  isReel = false, aspectRatio = 1.21f),
            ExploreItem("e50", "https://picsum.photos/seed/620/400/520",  isReel = false, aspectRatio = 0.77f),
            ExploreItem("e51", "https://picsum.photos/seed/621/400/400",  isReel = true,  aspectRatio = 1.00f),
            ExploreItem("e52", "https://picsum.photos/seed/622/400/640",  isReel = false, aspectRatio = 0.63f),
            ExploreItem("e53", "https://picsum.photos/seed/623/400/360",  isReel = false, aspectRatio = 1.11f),
            ExploreItem("e54", "https://picsum.photos/seed/624/400/480",  isReel = true,  aspectRatio = 0.83f),
            ExploreItem("e55", "https://picsum.photos/seed/625/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e56", "https://picsum.photos/seed/626/400/610",  isReel = false, aspectRatio = 0.66f),
            ExploreItem("e57", "https://picsum.photos/seed/627/400/290",  isReel = true,  aspectRatio = 1.38f),
            ExploreItem("e58", "https://picsum.photos/seed/628/400/550",  isReel = false, aspectRatio = 0.73f),
            ExploreItem("e59", "https://picsum.photos/seed/629/400/400",  isReel = false, aspectRatio = 1.00f),
            ExploreItem("e60", "https://picsum.photos/seed/630/400/460",  isReel = true,  aspectRatio = 0.87f),
        )
    }

    // ── Greedy shortest-column-first distribution ─────────────────────────────
    // Each item is assigned to the column with the LEAST accumulated height at that moment.
    // Result: all 3 columns end at nearly the same height → NO white space at bottom.
    val columns = remember(allItems) { distributeToColumns(allItems, numColumns = 3) }

    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ExploreTopBar(query = searchQuery, onQuery = { searchQuery = it })
        },
        bottomBar = {
            FeedBottomNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            columns.forEach { colItems ->
                MasonryColumn(
                    modifier = Modifier.weight(1f),
                    items    = colItems
                )
            }
        }
    }
}

// ── One masonry column ─────────────────────────
@Composable
fun MasonryColumn(
    modifier : Modifier,
    items    : List<ExploreItem>
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items.forEach { item ->
            ExploreGridItem(item = item)
        }
    }
}

// ── White top bar with grey search pill ───────
@Composable
fun ExploreTopBar(
    query   : String,
    onQuery : (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector        = Icons.Default.Search,
                        contentDescription = "Search",
                        tint               = Color(0xFF555555),
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value         = query,
                        onValueChange = onQuery,
                        singleLine    = true,
                        textStyle     = TextStyle(
                            color    = Color(0xFF1A1A1A),
                            fontSize = 15.sp
                        ),
                        modifier      = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (query.isEmpty()) {
                                Text(
                                    text     = "Search",
                                    color    = Color(0xFF888888),
                                    fontSize = 15.sp
                                )
                            }
                            inner()
                        }
                    )
                }
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}

// ── Individual masonry tile ────────────────────
@Composable
fun ExploreGridItem(item: ExploreItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(item.aspectRatio)
            .background(Color(0xFFE8E8E8))
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* TODO: open post/reel detail */ }
    ) {
        AsyncImage(
            model              = item.imageUrl,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // Reel badge — top-right corner
        if (item.isReel) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
            ) {
                Icon(
                    painter            = painterResource(id = R.drawable.ic_reel_outlined),
                    contentDescription = "Reel",
                    tint               = Color.White,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExploreScreenPreview() {
    ExploreContent()
}