package com.example.feed.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.feed.Repository.AuthRepository
import com.example.feed.navigation.Screen

// ─────────────────────────────────────────────────────────────────────────────
//  Data model for a single settings row
// ─────────────────────────────────────────────────────────────────────────────

data class SettingsItem(
    val icon       : ImageVector,
    val label      : String,
    val labelColor : Color = Color(0xFF1A1A1A),
    val iconTint   : Color = Color(0xFF1A1A1A),
    val onClick    : () -> Unit = {}
)

// ─────────────────────────────────────────────────────────────────────────────
//  SettingsScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(navController: NavController) {

    var showLogoutDialog       by remember { mutableStateOf(false) }
    var showDeleteDialog       by remember { mutableStateOf(false) }
    var showSwitchAccountSheet by remember { mutableStateOf(false) }

    // ── Section: Who can see your content ─────────────────────────────────
    val whoCanSeeItems = listOf(
        SettingsItem(Icons.Outlined.Lock,         "Account privacy"),
        SettingsItem(Icons.Outlined.VisibilityOff,"Close Friends"),
        SettingsItem(Icons.Outlined.PersonOff,    "Blocked"),
        SettingsItem(Icons.Outlined.HideSource,   "Muted accounts"),
        SettingsItem(Icons.Outlined.WifiOff,      "Hide story and live"),
    )

    // ── Section: How others can interact ──────────────────────────────────
    val howOthersInteractItems = listOf(
        SettingsItem(Icons.Outlined.Comment,       "Comments"),
        SettingsItem(Icons.Outlined.TagFaces,      "Tags and mentions"),
        SettingsItem(Icons.Outlined.Share,         "Sharing and remixes"),
        SettingsItem(Icons.Outlined.ThumbUpOffAlt, "Like and view counts"),
        SettingsItem(Icons.Outlined.AttachMoney,   "Payments"),
    )

    // ── Section: Connections ──────────────────────────────────────────────
    val connectionItems = listOf(
        SettingsItem(Icons.Outlined.NotificationsNone, "Notifications"),
        SettingsItem(Icons.Outlined.Message,           "Messages and story replies"),
        SettingsItem(Icons.Outlined.Email,             "Email notifications"),
    )

    // ── Section: Your app and media  ──────────────────────────────────────
    //   ↓  "Saved posts" row added here — tapping it opens SavedScreen
    val yourAppItems = listOf(
        SettingsItem(
            icon    = Icons.Outlined.Bookmark,
            label   = "Saved posts",
            onClick = { navController.navigate(Screen.Saved.route) }   // ← navigation
        ),
        SettingsItem(Icons.Outlined.SaveAlt,      "Archiving and downloading"),
        SettingsItem(Icons.Outlined.AccessTime,   "Time management"),
        SettingsItem(Icons.Outlined.Language,     "Language"),
        SettingsItem(Icons.Outlined.Contacts,     "Contacts syncing"),
        SettingsItem(Icons.Outlined.DarkMode,     "Accessibility"),
        SettingsItem(Icons.Outlined.DataUsage,    "Data usage and media quality"),
        SettingsItem(Icons.Outlined.PhoneAndroid, "Website permissions"),
    )

    // ── Section: More info & support ──────────────────────────────────────
    val moreInfoItems = listOf(
        SettingsItem(Icons.AutoMirrored.Outlined.Help, "Help"),
        SettingsItem(Icons.Outlined.Info,              "About"),
        SettingsItem(Icons.Outlined.Policy,            "Privacy policy"),
        SettingsItem(Icons.Outlined.Gavel,             "Terms of use"),
    )

    Scaffold(
        containerColor      = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SettingsTopBar(onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionHeader("Who can see your content")
            SettingsSectionCard(whoCanSeeItems)

            SettingsSectionHeader("How others can interact with you")
            SettingsSectionCard(howOthersInteractItems)

            SettingsSectionHeader("Connections")
            SettingsSectionCard(connectionItems)

            SettingsSectionHeader("Your app and media")
            SettingsSectionCard(yourAppItems)       // ← contains "Saved posts"

            SettingsSectionHeader("More info and support")
            SettingsSectionCard(moreInfoItems)

            Spacer(Modifier.height(16.dp))

            // ── Account actions ───────────────────────────────────────────
            SettingsSectionCard(
                items = listOf(
                    SettingsItem(
                        icon    = Icons.AutoMirrored.Outlined.Login,
                        label   = "Add account",
                        onClick = { showSwitchAccountSheet = true }
                    ),
                    SettingsItem(
                        icon       = Icons.AutoMirrored.Outlined.Logout,
                        label      = "Log out",
                        labelColor = Color(0xFFE53935),
                        iconTint   = Color(0xFFE53935),
                        onClick    = { showLogoutDialog = true }
                    ),
                    SettingsItem(
                        icon       = Icons.Outlined.DeleteOutline,
                        label      = "Delete account",
                        labelColor = Color(0xFFE53935),
                        iconTint   = Color(0xFFE53935),
                        onClick    = { showDeleteDialog = true }
                    ),
                )
            )

            Spacer(Modifier.height(32.dp))
        }
    }

    // ── Logout dialog ─────────────────────────────────────────────────────
    if (showLogoutDialog) {
        SettingsAlertDialog(
            title       = "Log out",
            message     = "Are you sure you want to log out?",
            confirmText = "Log out",
            onConfirm   = {
                showLogoutDialog = false
                AuthRepository().logout()
                navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // ── Delete account dialog ─────────────────────────────────────────────
    if (showDeleteDialog) {
        SettingsAlertDialog(
            title       = "Delete account",
            message     = "This will permanently delete your account and all your data. This action cannot be undone.",
            confirmText = "Delete",
            onConfirm   = {
                showDeleteDialog = false
                navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // ── Switch / add account sheet ────────────────────────────────────────
    if (showSwitchAccountSheet) {
        SwitchAccountSheet(
            onDismiss    = { showSwitchAccountSheet = false },
            onAddAccount = {
                showSwitchAccountSheet = false
                navController.navigate(Screen.Login.route)
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Top bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsTopBar(onBackClick: () -> Unit = {}) {
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
                text       = "Settings and activity",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A)
            )
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Section header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text     = title,
        fontSize = 12.sp,
        color    = Color(0xFF888888),
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 6.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Section card (white rounded card containing a list of rows)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsSectionCard(items: List<SettingsItem>) {
    Surface(
        modifier      = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape         = RoundedCornerShape(12.dp),
        color         = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingsRow(item = item)
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier  = Modifier.padding(start = 60.dp),
                        color     = Color(0xFFF2F2F2),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Single settings row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsRow(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = item.icon,
                contentDescription = item.label,
                tint               = item.iconTint,
                modifier           = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text       = item.label,
            fontSize   = 14.sp,
            color      = item.labelColor,
            fontWeight = FontWeight.Normal,
            modifier   = Modifier.weight(1f)
        )
        Icon(
            imageVector        = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint               = Color(0xFFCCCCCC),
            modifier           = Modifier.size(18.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Alert dialog (logout / delete)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsAlertDialog(
    title       : String,
    message     : String,
    confirmText : String,
    onConfirm   : () -> Unit,
    onDismiss   : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        shape            = RoundedCornerShape(16.dp),
        title            = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color(0xFF1A1A1A)) },
        text             = { Text(message, fontSize = 14.sp, color = Color(0xFF555555), lineHeight = 20.sp) },
        confirmButton    = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF888888), fontSize = 15.sp)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Switch / Add Account bottom sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchAccountSheet(
    onDismiss    : () -> Unit,
    onAddAccount : () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDDDDDD))
            )
        }
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            Text(
                text     = "Switch accounts",
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 12.dp)
            )

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Current account
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter            = rememberAsyncImagePainter("https://picsum.photos/seed/profile_main/200/200"),
                    contentDescription = "avatar",
                    modifier           = Modifier.size(44.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("andrew_mundy", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    Text("Andrew Mundy",  fontSize = 12.sp, color = Color(0xFF888888))
                }
                Box(
                    modifier         = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF0095F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))

            // Add account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onAddAccount() }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add", tint = Color(0xFF0095F6), modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Add account", fontSize = 14.sp, color = Color(0xFF0095F6), fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}