package com.example.feed.View.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.feed.R

// ── Bottom Nav Items ──────────────────────────
// For Reels we use a custom drawable instead of a Material Icon
// because the clapperboard/video-player style icon doesn't exist
// in material-icons-core and is only in material-icons-extended.
//
// Place your two drawables in res/drawable/:
//   ic_reels_outlined.xml  ← unselected state  (the icon from your screenshot)
//   ic_reels_filled.xml    ← selected state    (filled version)

sealed class BottomNavItem(val route: String, val label: String) {

    // Items that use a Material ImageVector (no drawable needed)
    sealed class VectorItem(
        route          : String,
        val selectedIcon  : ImageVector,
        val unselectedIcon: ImageVector,
        label          : String
    ) : BottomNavItem(route, label)

    // Items that use a drawable resource (Reels uses this)
    sealed class DrawableItem(
        route              : String,
        val selectedRes    : Int,
        val unselectedRes  : Int,
        label              : String
    ) : BottomNavItem(route, label)

    object Home    : VectorItem("feed",    Icons.Filled.Home,       Icons.Outlined.Home,    "Home")
    object Search  : VectorItem("explore", Icons.Filled.Search,     Icons.Outlined.Search,  "Search")
    object Add     : VectorItem("upload",  Icons.Filled.AddBox,     Icons.Outlined.AddBox,  "Add")
    object Profile : VectorItem("profile", Icons.Filled.Person,     Icons.Outlined.Person,  "Profile")

    // ✅ Uses your two drawables: ic_reels_filled + ic_reels_outlined
    object Reels   : DrawableItem(
        route        = "reels",
        selectedRes  = R.drawable.ic_reel_outlined,    // your filled drawable
        unselectedRes= R.drawable.ic_reel_filled,  // your outlined drawable (the one in screenshot)
        label        = "Reels"
    )
}

@Composable
fun FeedBottomNavBar(
    selectedRoute: String? = "feed",
    onItemClick: (String) -> Unit = {}
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Add,
        BottomNavItem.Reels,
        BottomNavItem.Profile
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

        Row(
            modifier              = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute == item.route
                val iconTint   = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF1A1A1A)
                val iconSize   = if (item == BottomNavItem.Add) 32.dp else 26.dp

                Box(
                    modifier         = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onItemClick(item.route) },
                    contentAlignment = Alignment.Center
                ) {
                    when (item) {
                        // ── Vector icons (Home, Search, Add, Profile) ──────
                        is BottomNavItem.VectorItem -> {
                            Icon(
                                imageVector        = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint               = iconTint,
                                modifier           = Modifier.size(iconSize)
                            )
                        }
                        // ── Drawable icons (Reels) ─────────────────────────
                        is BottomNavItem.DrawableItem -> {
                            Icon(
                                painter            = painterResource(
                                    id = if (isSelected) item.selectedRes else item.unselectedRes
                                ),
                                contentDescription = item.label,
                                tint               = iconTint,
                                modifier           = Modifier.size(iconSize)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedBottomNavBarPreview() {
    FeedBottomNavBar(selectedRoute = "feed")
}