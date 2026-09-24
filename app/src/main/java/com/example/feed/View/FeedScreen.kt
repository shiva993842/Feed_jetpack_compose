package com.example.feed.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.feed.Model.PostModel
import com.example.feed.Repository.AuthRepository
import com.example.feed.View.components.FeedBottomNavBar
import com.example.feed.View.components.FeedTopBar
import com.example.feed.View.components.PostCard
import com.example.feed.navigation.Screen

@Composable
fun FeedScreen(navController: NavController) {
    val repository    = remember { AuthRepository() }
    val currentRoute  by navController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route ?: Screen.Feed.route

    val dummyPosts = remember {
        listOf(
            PostModel("1",  "user1",  "ronaldo_23",    "", "https://picsum.photos/seed/1/500/500",   "Beautiful day! 🌟 Loving every moment",           "Hyderabad, India",  listOf("user1","user2","user3"),                       12, System.currentTimeMillis() - 3600000),
            PostModel("2",  "user2",  "shiva_photos",  "", "https://picsum.photos/seed/2/500/500",   "Amazing sunset view 🌅 Nature is beautiful",       "Mumbai, India",     listOf("user1"),                                       5,  System.currentTimeMillis() - 7200000),
            PostModel("3",  "user3",  "travel_vibes",  "", "https://picsum.photos/seed/3/500/500",   "Adventure awaits! 🏔️ #travel #explore",           "Goa, India",        listOf("user1","user2","user3","user4","user5"),       24,  System.currentTimeMillis() - 86400000),
            PostModel("4",  "user4",  "food_lover",    "", "https://picsum.photos/seed/4/500/500",   "Delicious biryani! 🍛 Best food in town",          "Chennai, India",    listOf("user2","user3"),                               8,  System.currentTimeMillis() - 172800000),
            PostModel("5",  "user5",  "city_life",     "", "https://picsum.photos/seed/5/500/500",   "City never sleeps ✨ #nightlife #city",            "Bangalore, India",  listOf("user1","user4","user5"),                       15, System.currentTimeMillis() - 259200000),
            PostModel("6",  "user6",  "nature_clicks", "", "https://picsum.photos/seed/42/500/500",  "Lost in the mountains 🏔️ #nature #hiking",        "Manali, India",     listOf("user1","user3","user6"),                       31, System.currentTimeMillis() - 320000000),
            PostModel("7",  "user7",  "delhi_diaries", "", "https://picsum.photos/seed/77/500/500",  "Street food is life 🌮🔥 #foodie #delhi",          "Delhi, India",      listOf("user2","user4","user5","user7"),               19, System.currentTimeMillis() - 400000000),
            PostModel("8",  "user8",  "ocean_vibes",   "", "https://picsum.photos/seed/88/500/500",  "Waves and sunsets 🌊🌅 Nothing better than this",  "Kovalam, India",    listOf("user1","user2"),                               7,  System.currentTimeMillis() - 450000000),
            PostModel("9",  "user9",  "art_by_priya",  "", "https://picsum.photos/seed/99/500/500",  "New canvas, new vibes 🎨 #art #creative",          "Pune, India",       listOf("user3","user5","user6","user8","user9"),       43, System.currentTimeMillis() - 500000000),
            PostModel("10", "user10", "fitness_raj",   "", "https://picsum.photos/seed/101/500/500", "Morning grind 💪 No days off! #gym #fitness",      "Hyderabad, India",  listOf("user1","user7","user10"),                      22, System.currentTimeMillis() - 550000000),
            PostModel("11", "user11", "sky_watcher",   "", "https://picsum.photos/seed/115/500/500", "Stargazing at 2AM 🌌✨ #astronomy #nightsky",       "Coorg, India",      listOf("user2","user3","user4","user6","user9"),       57, System.currentTimeMillis() - 600000000),
            PostModel("12", "user12", "biker_karan",   "", "https://picsum.photos/seed/130/500/500", "1000 km ride done 🏍️🔥 #roadtrip #bikelife",       "Leh, India",        listOf("user5","user8","user10","user12"),             38, System.currentTimeMillis() - 650000000),
            PostModel("13", "user13", "cafe_hoppers",  "", "https://picsum.photos/seed/145/500/500", "Best cappuccino in town ☕ #cafe #coffeelover",    "Bangalore, India",  listOf("user1","user3","user11"),                      14, System.currentTimeMillis() - 700000000),
            PostModel("14", "user14", "portrait_dev",  "", "https://picsum.photos/seed/160/500/500", "Golden hour magic 📸 #portrait #photography",     "Jaipur, India",     listOf("user2","user6","user7","user13","user14"),     66, System.currentTimeMillis() - 750000000),
            PostModel("15", "user15", "reads_by_mia",  "", "https://picsum.photos/seed/175/500/500", "Currently reading 📚 Can't put it down #books",   "Chennai, India",    listOf("user4","user9","user15"),                      9,  System.currentTimeMillis() - 800000000),
            PostModel("16", "user16", "kiran_snaps",    "", "https://picsum.photos/seed/201/500/500", "Weekend getaway was everything 🙌 #travel #vibes",  "Hyderabad, India", listOf("user1","user2","user5","user8","user16"),      91, System.currentTimeMillis() - 850000000),
            PostModel("17", "user17", "arjun_clicks",   "", "https://picsum.photos/seed/213/500/500", "Some moments are just too good 😍 #life #memories", "Vijayawada, India",listOf("user3","user6","user17"),                      28, System.currentTimeMillis() - 900000000),
            PostModel("18", "user18", "divya_captures", "", "https://picsum.photos/seed/225/500/500", "Golden hour hits different every time ✨",           "Bangalore, India", listOf("user1","user4","user7","user9","user14","user18"),134,System.currentTimeMillis() - 950000000),
            PostModel("19", "user19", "rahul_frames",   "", "https://picsum.photos/seed/237/500/500", "Living my best life 🌺🔥 #explore #wanderlust",     "Chennai, India",   listOf("user2","user5","user8","user11","user19"),     77, System.currentTimeMillis() - 1000000000),
            PostModel("20", "user20", "meera_shots",    "", "https://picsum.photos/seed/249/500/500", "Nature never gets old 🌿💚 #nature #peaceful",      "Vizag, India",     listOf("user3","user6","user10","user13","user20"),    56, System.currentTimeMillis() - 1050000000),
            PostModel("21", "user21", "vikram_lens",    "", "https://picsum.photos/seed/261/500/500", "Every shot tells a story 📷 #streetphotography",   "Hyderabad, India", listOf("user1","user4","user7","user12","user15","user21"),112,System.currentTimeMillis() - 1100000000),
            PostModel("22", "user22", "ananya_diaries", "", "https://picsum.photos/seed/273/500/500", "Just another beautiful day 😊☀️ #happy #grateful", "Mumbai, India",    listOf("user2","user5","user9","user16","user22"),     43, System.currentTimeMillis() - 1150000000),
            PostModel("23", "user23", "rohan_travels",  "", "https://picsum.photos/seed/285/500/500", "New city new adventures 🗺️✈️ #travel #explore",   "Delhi, India",     listOf("user3","user6","user11","user17","user23"),    88, System.currentTimeMillis() - 1200000000),
            PostModel("24", "user24", "priya_moments",  "", "https://picsum.photos/seed/297/500/500", "Chasing light and good times 🌅📸 #sunset",        "Pune, India",      listOf("user1","user8","user13","user18","user24"),    61, System.currentTimeMillis() - 1250000000),
            PostModel("25", "user25", "aditya_vision",  "", "https://picsum.photos/seed/309/500/500", "This view though 😭🤌 absolutely breathtaking",    "Bangalore, India", listOf("user2","user7","user12","user19","user25"),    74, System.currentTimeMillis() - 1300000000),
        )
    }

    FeedContent(
        posts          = dummyPosts,
        selectedRoute  = selectedRoute,
        currentUserId  = repository.getCurrentUser()?.uid ?: "",
        onLogoutClick  = {
            repository.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Feed.route) { inclusive = true }
            }
        },
        onNavItemClick = { route ->
            if (route != selectedRoute) navController.navigate(route)
        },
        onMessageClick = {
            navController.navigate(Screen.Messages.route)
        },
        // Tapping avatar/username in any PostCard opens FriendsProfileScreen
        onProfileClick = { userId, username ->
            navController.navigate(Screen.FriendsProfile.withArgs(userId, username))
        }
    )
}

@Composable
fun FeedContent(
    posts          : List<PostModel>                  = emptyList(),
    selectedRoute  : String                           = "feed",
    currentUserId  : String                           = "",
    onLogoutClick  : () -> Unit                       = {},
    onNavItemClick : (String) -> Unit                 = {},
    onMessageClick : () -> Unit                       = {},
    onProfileClick : (userId: String, username: String) -> Unit = { _, _ -> }
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            FeedTopBar(
                onNotificationClick = { },
                onMessageClick      = onMessageClick
            )
        },
        bottomBar = {
            FeedBottomNavBar(
                selectedRoute = selectedRoute,
                onItemClick   = onNavItemClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (posts.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No posts yet!", color = Color(0xFF888888), fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().background(Color.White),
                contentPadding = paddingValues
            ) {
                items(items = posts, key = { it.postId }) { post ->
                    PostCard(
                        post           = post,
                        currentUserId  = currentUserId,
                        onSaveClick    = { },
                        // Pass userId + username so PostCard can call back with them
                        onProfileClick = { userId, username ->
                            onProfileClick(userId, username)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    FeedContent(
        posts = listOf(
            PostModel("1", "user1", "ronaldo_23",   "", "", "Beautiful day! 🌟", "Hyderabad, India", listOf("user1","user2","user3"), 12, System.currentTimeMillis() - 3600000),
            PostModel("2", "user2", "shiva_photos", "", "", "Amazing sunset 🌅",  "Mumbai, India",    listOf("user1"),               5,  System.currentTimeMillis() - 7200000),
        )
    )
}