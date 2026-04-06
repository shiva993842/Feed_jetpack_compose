package com.example.feed.View.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.feed.ui.theme.Primary

// ── Share to user data ────────────────────────
data class ShareUser(val username: String, val initial: String, val color: Color = Primary)

val shareUsers = listOf(
    ShareUser("ronaldo_23",   "R", Color(0xFF6C63FF)),
    ShareUser("shiva_photos", "S", Color(0xFF00B894)),
    ShareUser("travel_vibes", "T", Color(0xFFE17055)),
    ShareUser("food_lover",   "F", Color(0xFFFDAA44)),
    ShareUser("city_life",    "C", Color(0xFF0984E3)),
    ShareUser("marklavern",   "M", Color(0xFFD63031)),
    ShareUser("amberberry",   "A", Color(0xFF6C63FF)),
    ShareUser("priya_m",      "P", Color(0xFF00CEC9)),
)

// ── Social app tiles (static — matches the image: WhatsApp, Facebook, Instagram, Telegram) ──
data class SocialAppItem(
    val label   : String,
    val icon    : ImageVector,
    val bgColor : Color,
    val tint    : Color = Color.White
)

val socialApps = listOf(
    SocialAppItem("WhatsApp",  Icons.Outlined.Chat,         Color(0xFF25D366)),
    SocialAppItem("Facebook",  Icons.Outlined.Facebook,     Color(0xFF1877F2)),
    SocialAppItem("Instagram", Icons.Outlined.CameraAlt,    Color(0xFFE1306C)),
    SocialAppItem("Telegram",  Icons.Outlined.Send,         Color(0xFF2CA5E0)),
)

// ── "Others" section items ────────────────────
data class OtherShareItem(
    val label   : String,
    val icon    : ImageVector,
    val bgColor : Color,
    val tint    : Color = Color(0xFF1A1A1A)
)

// Copy to clipboard helper
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("link", text))
}

// Launch system share chooser
fun launchSystemShareSheet(context: Context, text: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share via"))
}

// ── Main ShareBottomSheet ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    shareText : String     = "Check out this post! https://example.com/post/1",
    onDismiss : () -> Unit = {}
) {
    val context    = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var linkCopied by remember { mutableStateOf(false) }

    val otherItems = listOf(
        OtherShareItem("Email",    Icons.Outlined.Email,   Color(0xFFF0F0F0)),
        OtherShareItem("Copy link",Icons.Outlined.Link,    Color(0xFFF0F0F0)),
        OtherShareItem("More",     Icons.Outlined.MoreHoriz, Color(0xFFF0F0F0)),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = Color.White,
        modifier         = Modifier.fillMaxWidth(),
        dragHandle = {
            // ── Drag handle only ──────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFDDDDDD))
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
        ) {

            // ── RECENT section ────────────────
            Text(
                text     = "Recent",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color    = Color(0xFF1A1A1A),
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(shareUsers.take(5)) { user ->
                    ShareUserAvatar(user = user)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── SOCIAL section ────────────────
            Text(
                text       = "Social",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A),
                modifier   = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(socialApps) { app ->
                    SocialAppTile(app = app, onClick = {
                        launchSystemShareSheet(context, shareText)
                    })
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── OTHERS section ────────────────
            Text(
                text       = "Others",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A),
                modifier   = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(otherItems) { item ->
                    OtherShareTile(
                        item    = item,
                        onClick = {
                            when (item.label) {
                                "Copy link" -> {
                                    copyToClipboard(context, shareText)
                                    linkCopied = true
                                }
                                else        -> launchSystemShareSheet(context, shareText)
                            }
                        },
                        label   = if (item.label == "Copy link" && linkCopied) "Copied!" else item.label
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Recent user avatar circle ─────────────────
@Composable
fun ShareUserAvatar(user: ShareUser) {
    var sent by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(60.dp)
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { sent = !sent }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(user.color.copy(alpha = 0.15f))
                .then(if (sent) Modifier.border(2.dp, user.color, CircleShape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = user.initial,
                color      = user.color,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text      = user.username,
            fontSize  = 11.sp,
            color     = Color(0xFF1A1A1A),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ── Social app tile (coloured icon square) ────
@Composable
fun SocialAppTile(app: SocialAppItem, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(64.dp)
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(app.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = app.icon,
                contentDescription = app.label,
                tint               = app.tint,
                modifier           = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text      = app.label,
            fontSize  = 11.sp,
            color     = Color(0xFF1A1A1A),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ── Others tile (grey square) ─────────────────
@Composable
fun OtherShareTile(item: OtherShareItem, label: String = item.label, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(64.dp)
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(item.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = item.icon,
                contentDescription = item.label,
                tint               = item.tint,
                modifier           = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text      = label,
            fontSize  = 11.sp,
            color     = Color(0xFF1A1A1A),
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}