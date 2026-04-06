package com.example.feed.View

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.videoFrameMillis
import com.example.feed.View.components.FeedBottomNavBar
import java.io.File

enum class UploadStep { GALLERY, PREVIEW, CAPTION }

enum class AspectMode(val label: String, val ratio: Float?) {
    ORIGINAL("1:1",  null),
    PORTRAIT("4:5",  4f / 5f),
    LANDSCAPE("16:9", 16f / 9f)
}

data class MediaItem(
    val uri     : Uri,
    val isVideo : Boolean = false,
    val duration: String  = ""
)

@Composable
fun UploadScreen(navController: NavController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val selectedRoute: String = currentBackStack?.destination?.route ?: "upload"
    UploadContent(
        selectedRoute  = selectedRoute,
        onNavItemClick = { route -> if (route != selectedRoute) navController.navigate(route) },
        onClose        = { navController.popBackStack() },
        onShare        = {
            navController.navigate("feed") {
                popUpTo("upload") { inclusive = true }
            }
        }
    )
}

@Composable
fun UploadContent(
    selectedRoute  : String           = "upload",
    onNavItemClick : (String) -> Unit = {},
    onClose        : () -> Unit       = {},
    onShare        : () -> Unit       = {}
) {
    val context = LocalContext.current

    var currentStep     by remember { mutableStateOf(UploadStep.GALLERY) }
    // ✅ null by default — no auto-select, user must tap an image
    var selectedUri     by remember { mutableStateOf<Uri?>(null) }
    var captionText     by remember { mutableStateOf(TextFieldValue("")) }
    var locationText    by remember { mutableStateOf("") }
    var selectedAlbum   by remember { mutableStateOf("Recents") }
    var showAlbumPicker by remember { mutableStateOf(false) }
    var selectedAspect  by remember { mutableStateOf(AspectMode.ORIGINAL) }
    var mediaItems      by remember { mutableStateOf<List<MediaItem>>(emptyList()) }

    // ── Storage permissions (images + videos) ─
    val storagePerms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    else
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    val hasStoragePerm = storagePerms.all {
        ContextCompat.checkSelfPermission(context, it) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // ✅ Camera permission tracked separately
    var hasCameraPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    // Storage launcher
    val storageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) mediaItems = loadGalleryMedia(context)
    }

    // ✅ Camera permission launcher
    // We store the URI we want to shoot to, then launch camera once granted
    var pendingCamUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPerm = granted }

    // Camera capture launcher
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            selectedUri = cameraUri
            mediaItems  = listOf(MediaItem(uri = cameraUri!!)) + mediaItems
        }
    }

    // ✅ Once camera permission granted + pending uri ready → launch camera
    LaunchedEffect(hasCameraPerm, pendingCamUri) {
        if (hasCameraPerm && pendingCamUri != null) {
            cameraUri    = pendingCamUri
            pendingCamUri = null
            cameraLauncher.launch(cameraUri!!)
        }
    }

    // Load storage on first compose
    LaunchedEffect(Unit) {
        if (hasStoragePerm) mediaItems = loadGalleryMedia(context)
        else storageLauncher.launch(storagePerms)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            UploadTopBar(
                step        = currentStep,
                onBack      = {
                    when (currentStep) {
                        UploadStep.GALLERY -> onClose()
                        UploadStep.PREVIEW -> currentStep = UploadStep.GALLERY
                        UploadStep.CAPTION -> currentStep = UploadStep.PREVIEW
                    }
                },
                onNext      = {
                    when (currentStep) {
                        UploadStep.GALLERY -> if (selectedUri != null) currentStep = UploadStep.PREVIEW
                        UploadStep.PREVIEW -> currentStep = UploadStep.CAPTION
                        UploadStep.CAPTION -> onShare()
                    }
                },
                nextLabel   = when (currentStep) {
                    UploadStep.GALLERY -> "Next"
                    UploadStep.PREVIEW -> "Next"
                    UploadStep.CAPTION -> "Share"
                },
                nextEnabled = selectedUri != null
            )
        },
        bottomBar = {
            FeedBottomNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
        },
        containerColor = Color.White
    ) { paddingValues ->
        when (currentStep) {
            UploadStep.GALLERY -> GalleryStep(
                paddingValues   = paddingValues,
                mediaItems      = mediaItems,
                selectedUri     = selectedUri,
                selectedAlbum   = selectedAlbum,
                showAlbumPicker = showAlbumPicker,
                selectedAspect  = selectedAspect,
                onSelect        = { selectedUri = it },
                onAlbumClick    = { showAlbumPicker = !showAlbumPicker },
                onAlbumSelect   = { selectedAlbum = it; showAlbumPicker = false },
                onAspectChange  = { selectedAspect = it },
                onCameraClick   = {
                    // ✅ Always create URI first
                    val tmpFile = File.createTempFile("cam_", ".jpg", context.cacheDir)
                    val uri     = FileProvider.getUriForFile(
                        context, "${context.packageName}.provider", tmpFile
                    )
                    pendingCamUri = uri
                    if (hasCameraPerm) {
                        // Already have permission — launch directly
                        cameraUri     = uri
                        pendingCamUri = null
                        cameraLauncher.launch(uri)
                    } else {
                        // ✅ Request permission first — camera launches via LaunchedEffect
                        cameraPermLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
            UploadStep.PREVIEW -> PreviewStep(
                paddingValues  = paddingValues,
                selectedUri    = selectedUri,
                selectedAspect = selectedAspect,
                onAspectChange = { selectedAspect = it }
            )
            UploadStep.CAPTION -> CaptionStep(
                paddingValues    = paddingValues,
                selectedUri      = selectedUri,
                captionText      = captionText,
                locationText     = locationText,
                onCaptionChange  = { captionText  = it },
                onLocationChange = { locationText = it }
            )
        }
    }
}

@Composable
fun UploadTopBar(step: UploadStep, onBack: () -> Unit, onNext: () -> Unit, nextLabel: String, nextEnabled: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White).statusBarsPadding()) {
        Row(
            modifier              = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1A1A1A), modifier = Modifier.size(26.dp))
            }
            Text(
                text       = when (step) {
                    UploadStep.GALLERY -> "New post"
                    UploadStep.PREVIEW -> "Crop"
                    UploadStep.CAPTION -> "New post"
                },
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A)
            )
            TextButton(onClick = onNext, enabled = nextEnabled) {
                Text(
                    text       = nextLabel,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (nextEnabled) Color(0xFF0095F6) else Color(0xFF0095F6).copy(alpha = 0.4f)
                )
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}

@Composable
fun GalleryStep(
    paddingValues   : PaddingValues,
    mediaItems      : List<MediaItem>,
    selectedUri     : Uri?,
    selectedAlbum   : String,
    showAlbumPicker : Boolean,
    selectedAspect  : AspectMode,
    onSelect        : (Uri) -> Unit,
    onAlbumClick    : () -> Unit,
    onAlbumSelect   : (String) -> Unit,
    onAspectChange  : (AspectMode) -> Unit,
    onCameraClick   : () -> Unit = {}
) {
    // ✅ Expandable preview fraction — drag to resize
    var previewFraction by remember { mutableFloatStateOf(0.45f) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(paddingValues)) {

        // ── Large expandable preview ──────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(previewFraction)
                .background(Color.Black)
        ) {
            if (selectedUri != null) {
                AsyncImage(
                    model              = selectedUri,
                    contentDescription = "Preview",
                    contentScale       = when (selectedAspect) {
                        AspectMode.ORIGINAL  -> ContentScale.Crop
                        AspectMode.PORTRAIT  -> ContentScale.Fit
                        AspectMode.LANDSCAPE -> ContentScale.Fit
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Aspect pills — bottom left
            Row(
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AspectMode.entries.forEach { mode ->
                    val sel = selectedAspect == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (sel) Color.White else Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, if (sel) Color.Transparent else Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onAspectChange(mode) }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mode.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (sel) Color.Black else Color.White)
                    }
                }
            }

            // Multi-select — bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd).padding(10.dp)
                    .size(34.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LibraryAdd, "Select multiple", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // ✅ Drag handle to resize preview up/down
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(Color.White)
                .draggable(
                    orientation = Orientation.Vertical,
                    state       = rememberDraggableState { delta ->
                        previewFraction = (previewFraction + delta / 1800f).coerceIn(0.20f, 0.65f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFDDDDDD)))
        }

        // ── Album header ─────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth().height(46.dp)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onAlbumClick() }
                .padding(horizontal = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(selectedAlbum, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Icon(
                    if (showAlbumPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallIconBtn(Icons.Default.CameraAlt, "Camera", onCameraClick)
                SmallIconBtn(Icons.Default.GridView,  "Grid",   {})
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

        AnimatedVisibility(visible = showAlbumPicker) { AlbumDropdown(onSelect = onAlbumSelect) }

        // ── Grid ─────────────────────────────
        if (mediaItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Image, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No photos yet", color = Color(0xFF888888), fontSize = 15.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Fixed(4),
                modifier              = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement   = Arrangement.spacedBy(2.dp),
                contentPadding        = PaddingValues(top = 2.dp)
            ) {
                item { CameraTile(onClick = onCameraClick) }
                items(mediaItems, key = { it.uri.toString() }) { item ->
                    GalleryThumbnail(item = item, isSelected = item.uri == selectedUri, onSelect = { onSelect(item.uri) })
                }
            }
        }
    }
}

@Composable
fun SmallIconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(32.dp).clip(CircleShape).background(Color(0xFFF2F2F2))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, desc, tint = Color(0xFF1A1A1A), modifier = Modifier.size(17.dp))
    }
}

@Composable
fun AlbumDropdown(onSelect: (String) -> Unit) {
    val albums = listOf("Recents", "Favourites", "Downloads", "Screenshots", "WhatsApp", "Instagram", "Camera")
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        albums.forEach { album ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(album) }.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFE8E8E8)))
                Text(album, fontSize = 15.sp, color = Color(0xFF1A1A1A), fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp))
        }
    }
}

@Composable
fun CameraTile(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.aspectRatio(1f).background(Color(0xFF1A1A1A)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CameraAlt, "Camera", tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(4.dp))
            Text("Camera", color = Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
fun GalleryThumbnail(item: MediaItem, isSelected: Boolean, onSelect: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .aspectRatio(1f).background(Color(0xFFE0E0E0))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSelect() }
    ) {
        if (item.isVideo) {
            // ✅ For videos use ImageRequest with VIDEO_FRAME_MICROS to get thumbnail
            AsyncImage(
                model = coil.request.ImageRequest.Builder(context)
                    .data(item.uri)
                    .videoFrameMillis(0)          // grab first frame
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model              = item.uri,
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        }

        // Video duration badge
        if (item.isVideo) {
            // Dark scrim at bottom for readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                        )
                    )
            )
            Row(
                modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Text(item.duration, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Selected overlay
        if (isSelected) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0095F6).copy(alpha = 0.25f)))
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(22.dp).clip(CircleShape).background(Color(0xFF0095F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Composable
fun PreviewStep(paddingValues: PaddingValues, selectedUri: Uri?, selectedAspect: AspectMode, onAspectChange: (AspectMode) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(paddingValues)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(when (selectedAspect) {
                    AspectMode.PORTRAIT  -> 4f / 5f
                    AspectMode.LANDSCAPE -> 16f / 9f
                    AspectMode.ORIGINAL  -> 1f
                })
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (selectedUri != null) {
                AsyncImage(selectedUri, "Preview", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Adjust crop", fontSize = 13.sp, color = Color(0xFF888888), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AspectMode.entries.forEach { mode ->
                val sel = selectedAspect == mode
                Column(
                    modifier = Modifier
                        .weight(1f).clip(RoundedCornerShape(10.dp))
                        .border(1.5.dp, if (sel) Color(0xFF1A1A1A) else Color(0xFFDDDDDD), RoundedCornerShape(10.dp))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onAspectChange(mode) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val (w, h) = when (mode) {
                        AspectMode.PORTRAIT  -> 14.dp to 18.dp
                        AspectMode.LANDSCAPE -> 20.dp to 14.dp
                        AspectMode.ORIGINAL  -> 16.dp to 16.dp
                    }
                    Box(modifier = Modifier.width(w).height(h).border(1.5.dp, if (sel) Color(0xFF1A1A1A) else Color(0xFF888888), RoundedCornerShape(2.dp)))
                    Text(mode.label, fontSize = 12.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) Color(0xFF1A1A1A) else Color(0xFF888888))
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE),
            thickness = 0.5.dp)
        Spacer(Modifier.height(20.dp))
        Text("Edit", fontSize = 13.sp,
            color = Color(0xFF888888),
            modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(Icons.Default.Tune to "Adjust", Icons.Default.AutoFixHigh to "Filter", Icons.Outlined.WbSunny to "Brightness", Icons.Default.Contrast to "Contrast").forEach { (icon, label) ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF5F5F5)).clickable { }, contentAlignment = Alignment.Center) {
                        Icon(icon, label, tint = Color(0xFF1A1A1A), modifier = Modifier.size(22.dp))
                    }
                    Text(label, fontSize = 11.sp, color = Color(0xFF555555))
                }
            }
        }
    }
}

@Composable
fun CaptionStep(paddingValues: PaddingValues, selectedUri: Uri?, captionText: TextFieldValue, locationText: String, onCaptionChange: (TextFieldValue) -> Unit, onLocationChange: (String) -> Unit) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(paddingValues).verticalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFE0E0E0))) {
                if (selectedUri != null) AsyncImage(selectedUri, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = captionText, onValueChange = onCaptionChange,
                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 60.dp),
                textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF1A1A1A)),
                decorationBox = { inner ->
                    if (captionText.text.isEmpty()) Text("Write a caption...", fontSize = 15.sp, color = Color(0xFFAAAAAA))
                    inner()
                }
            )
            Icon(Icons.Outlined.EmojiEmotions, "Emoji", tint = Color(0xFF888888), modifier = Modifier.size(24.dp).align(Alignment.CenterVertically))
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        CaptionOptionRow(Icons.Outlined.LocationOn, if (locationText.isEmpty()) "Add location" else locationText, true, locationText, onLocationChange)
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        CaptionOptionRow(Icons.Default.PersonAdd, "Tag people")
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        CaptionOptionRow(Icons.Default.MusicNote, "Add music")
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
        CaptionOptionRow(Icons.Default.Settings, "Advanced settings")
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

        Spacer(Modifier.height(16.dp))
        Text("Also post to", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        listOf("Facebook", "Twitter / X", "Tumblr").forEach { platform ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFE8E8E8)))
                    Text(platform, fontSize = 15.sp, color = Color(0xFF1A1A1A))
                }
                var toggled by remember { mutableStateOf(false) }
                Switch(checked = toggled, onCheckedChange = { toggled = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF1A1A1A), uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFDDDDDD)))
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp, modifier = Modifier.padding(start = 60.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun CaptionOptionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isInput: Boolean = false, value: String = "", onValue: (String) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
        }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)) {
            Icon(icon, label,
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(22.dp))
            if (isInput) {
                BasicTextField(value = value,
                    onValueChange = onValue,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 15.sp,
                        color = Color(0xFF1A1A1A)),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner -> if (value.isEmpty())
                        Text(label, fontSize = 15.sp,
                            color = Color(0xFF888888)); inner() })
            } else {
                Text(label, fontSize = 15.sp, color = Color(0xFF888888))
            }
        }
        if (!isInput) Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
    }
}

// ✅ Load ALL images + ALL videos with no limits
fun loadGalleryMedia(context: Context): List<MediaItem> {
    val items = mutableListOf<MediaItem>()

    // Images
    val imgUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    try {
        context.contentResolver.query(imgUri,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_MODIFIED),
            null, null, "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (c.moveToNext()) {
                items.add(MediaItem(
                    uri     = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, c.getLong(idCol)),
                    isVideo = false
                ))
            }
        }
    } catch (_: Exception) {}

    // Videos
    val vidUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    else MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    try {
        context.contentResolver.query(vidUri,
            arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_MODIFIED),
            null, null, "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )?.use { c ->
            val idCol  = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val durCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (c.moveToNext()) {
                val secs = (c.getLong(durCol) / 1000).toInt()
                items.add(MediaItem(
                    uri      = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, c.getLong(idCol)),
                    isVideo  = true,
                    duration = if (secs >= 60) "${secs / 60}:${"%02d".format(secs % 60)}" else "0:${"%02d".format(secs)}"
                ))
            }
        }
     } catch (_: Exception) {}

    return items.sortedByDescending { it.uri.lastPathSegment }
 }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UploadScreenPreview() { UploadContent() }