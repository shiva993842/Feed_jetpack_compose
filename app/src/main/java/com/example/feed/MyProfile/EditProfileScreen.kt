package com.example.feed.MyProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.feed.ui.theme.Primary

// ─────────────────────────────────────────────────────────────────────────────
//  EditProfileScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EditProfileScreen(navController: NavController) {

    // ── State ─────────────────────────────────
    var name        by remember { mutableStateOf("Andrew Mundy")  }
    var username    by remember { mutableStateOf("andrew_mundy")  }
    var website     by remember { mutableStateOf("")              }
    var bio         by remember { mutableStateOf("📸 Photographer & traveler\n🌍 Hyderabad, India") }
    var gender      by remember { mutableStateOf("Prefer not to say") }
    var showGenderSheet by remember { mutableStateOf(false) }

    val genderOptions = listOf(
        "Male", "Female", "Non-binary",
        "Transgender", "Intersex",
        "Prefer not to say"
    )

    // ── Save handler (wire to ViewModel later) ─
    fun onSave() {
        // TODO: call viewModel.updateProfile(name, username, website, bio, gender)
        navController.popBackStack()
    }

    Scaffold(
        containerColor      = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            EditProfileTopBar(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { onSave() }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Avatar row ────────────────────────
            AvatarSection(onChangePicture = { /* TODO: open image picker */ })

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Name ──────────────────────────────
            EditField(
                label       = "Name",
                value       = name,
                onValueChange = { if (it.length <= 30) name = it },
                placeholder = "Name",
                imeAction   = ImeAction.Next
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Username ──────────────────────────
            EditField(
                label       = "Username",
                value       = username,
                onValueChange = { if (it.length <= 30) username = it },
                placeholder = "Username",
                imeAction   = ImeAction.Next
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Website ───────────────────────────
            EditField(
                label       = "Website",
                value       = website,
                onValueChange = { website = it },
                placeholder = "Website",
                imeAction   = ImeAction.Next
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Bio ───────────────────────────────
            BioField(
                value         = bio,
                onValueChange = { if (it.length <= 150) bio = it }
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Section header ────────────────────
            SectionHeader(title = "Private information")

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Gender (navigation row) ───────────
            GenderRow(
                gender  = gender,
                onClick = { showGenderSheet = true }
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)

            // ── Personal information link ─────────
            LinkRow(
                label = "Personal information",
                onClick = { /* navigate to personal info screen */ }
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
        }
    }

    // ── Gender bottom sheet ────────────────────
    if (showGenderSheet) {
        GenderBottomSheet(
            selected  = gender,
            options   = genderOptions,
            onSelect  = {
                gender          = it
                showGenderSheet = false
            },
            onDismiss = { showGenderSheet = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTopBar(
    onBackClick : () -> Unit,
    onSaveClick : () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF1A1A1A)
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = Color(0xFF1A1A1A)
                )
            }
        },
        title = {
            Text(
                text       = "Edit profile",
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A)
            )
        },
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = "Save",
                    tint               = Primary
                )
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  Avatar Section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AvatarSection(onChangePicture: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer box is slightly larger to accommodate the badge overlapping the edge
        Box(
            modifier         = Modifier.size(104.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile image circle
            AsyncImage(
                model              = "https://picsum.photos/seed/profile_main/200/200",
                contentDescription = "Profile picture",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFDDDDDD), CircleShape)
                    .align(Alignment.Center)
            )

            // Camera badge — bottom-right, half inside half outside the avatar
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Primary)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint               = Color.White,
                    modifier           = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text     = "Edit picture or avatar",
            color    = Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onChangePicture() }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Single-line Edit Field
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditField(
    label         : String,
    value         : String,
    onValueChange : (String) -> Unit,
    placeholder   : String,
    imeAction     : ImeAction = ImeAction.Done
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Normal,
            color      = Color(0xFF1A1A1A),
            modifier   = Modifier.width(90.dp)
        )

        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            singleLine    = true,
            textStyle     = TextStyle(
                fontSize  = 14.sp,
                color     = Color(0xFF1A1A1A)
            ),
            cursorBrush   = SolidColor(Primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction      = imeAction
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text     = placeholder,
                        fontSize = 14.sp,
                        color    = Color(0xFFAAAAAA)
                    )
                }
                inner()
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Bio Field (multi-line + char counter)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BioField(
    value         : String,
    onValueChange : (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text       = "Bio",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Normal,
                color      = Color(0xFF1A1A1A),
                modifier   = Modifier
                    .width(90.dp)
                    .paddingFromBaseline(top = 0.dp)
            )

            BasicTextField(
                value         = value,
                onValueChange = onValueChange,
                textStyle     = TextStyle(
                    fontSize    = 14.sp,
                    color       = Color(0xFF1A1A1A),
                    lineHeight  = 20.sp
                ),
                cursorBrush   = SolidColor(Primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 60.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            text     = "Bio",
                            fontSize = 14.sp,
                            color    = Color(0xFFAAAAAA)
                        )
                    }
                    inner()
                }
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text     = "${value.length} / 150",
            fontSize = 11.sp,
            color    = Color(0xFFAAAAAA),
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 0.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Section Header (grey label like "Private information")
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text       = title,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF888888)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Gender Row (tap to open sheet)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GenderRow(
    gender  : String,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = "Gender",
            fontSize   = 14.sp,
            color      = Color(0xFF1A1A1A),
            modifier   = Modifier.width(90.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.weight(1f)
        ) {
            Text(
                text     = gender,
                fontSize = 14.sp,
                color    = Color(0xFF888888),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = Color(0xFFAAAAAA),
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Link Row (e.g. "Personal information")
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LinkRow(
    label   : String,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication        = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = label,
            fontSize   = 14.sp,
            color      = Primary,
            fontWeight = FontWeight.Normal
        )
        Icon(
            imageVector        = Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = Color(0xFFAAAAAA),
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Gender Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderBottomSheet(
    selected  : String,
    options   : List<String>,
    onSelect  : (String) -> Unit,
    onDismiss : () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        containerColor      = Color.White,
        sheetState          = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle          = {
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
                text       = "Gender",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A1A),
                modifier   = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp)
            )

            HorizontalDivider(color = Color(0xFFF0F0F0))

            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onSelect(option) }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text     = option,
                        fontSize = 15.sp,
                        color    = Color(0xFF1A1A1A)
                    )

                    if (option == selected) {
                        Box(
                            modifier         = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Check,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color(0xFFDDDDDD), CircleShape)
                        )
                    }
                }

                HorizontalDivider(
                    color     = Color(0xFFF0F0F0),
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(navController = rememberNavController())
}