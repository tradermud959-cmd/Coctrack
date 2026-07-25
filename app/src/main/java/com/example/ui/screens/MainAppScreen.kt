package com.example.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.GemTransaction
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GemsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.animatedNeonBorder(
            isElectro = com.example.ui.theme.LocalAppTheme.current == "electro",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.secondary
        ),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: GemsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val username by viewModel.username.collectAsState()
    val successMsg by viewModel.successMessage.collectAsState()
    val activeAchievement by viewModel.activeAchievement.collectAsState()
    val context = LocalContext.current

    // Trigger toast notification on state update
    LaunchedEffect(successMsg) {
        successMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearNotification()
        }
    }

    // Handles initial splash loop
    LaunchedEffect(Unit) {
        if (currentScreen is AppScreen.Splash) {
            delay(2500) // Splash delay 2.5 seconds
            viewModel.setScreen(AppScreen.Dashboard)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            is AppScreen.Splash -> {
                SplashScreen()
            }
            else -> {
                Scaffold(
                    topBar = {
                        val title = when (currentScreen) {
                            AppScreen.Dashboard -> "COC F2P Gems Tracker"
                            AppScreen.UpdateGems -> "Input Gems Baru"
                            AppScreen.Statistics -> "Statistik Perolehan"
                            AppScreen.History -> "Riwayat Gems"
                            AppScreen.Target -> "Target Gems Impian"
                            AppScreen.OfflineAI -> "Analisa Heuristik AI"
                            AppScreen.Settings -> "Pengaturan"
                            AppScreen.About -> "Tentang Aplikasi"
                            AppScreen.Partner -> "Partner Desa"
                            else -> "Gems Tracker"
                        }
                        if (currentScreen != AppScreen.Dashboard) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                },
                                navigationIcon = {
                                    if (currentScreen != AppScreen.Dashboard) {
                                        IconButton(onClick = { viewModel.setScreen(AppScreen.Dashboard) }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Kembali ke Dashboard"
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != AppScreen.Splash) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .border(1.dp, Color.White.copy(alpha = 0.05f))
                                    .windowInsetsPadding(WindowInsets.navigationBars),
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.Dashboard,
                                    onClick = { viewModel.setScreen(AppScreen.Dashboard) },
                                    icon = { Icon(if (currentScreen == AppScreen.Dashboard) Icons.Default.Home else Icons.Outlined.Home, null) },
                                    label = { Text("Home", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(0xFF2D1F16),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.Statistics,
                                    onClick = { viewModel.setScreen(AppScreen.Statistics) },
                                    icon = { Icon(if (currentScreen == AppScreen.Statistics) Icons.Default.BarChart else Icons.Outlined.BarChart, null) },
                                    label = { Text("Stats", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(0xFF2D1F16),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.UpdateGems,
                                    onClick = { viewModel.setScreen(AppScreen.UpdateGems) },
                                    icon = { Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(28.dp)) },
                                    label = { Text("Update", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(0xFF2D1F16),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.History,
                                    onClick = { viewModel.setScreen(AppScreen.History) },
                                    icon = { Icon(if (currentScreen == AppScreen.History) Icons.Default.History else Icons.Outlined.History, null) },
                                    label = { Text("Riwayat", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(0xFF2D1F16),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.Settings,
                                    onClick = { viewModel.setScreen(AppScreen.Settings) },
                                    icon = { Icon(if (currentScreen == AppScreen.Settings) Icons.Default.Settings else Icons.Outlined.Settings, null) },
                                    label = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color(0xFF2D1F16),
                                        unselectedIconColor = Color(0xFF94A3B8),
                                        unselectedTextColor = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        val backgroundResId = when (currentScreen) {
                            AppScreen.Dashboard -> R.drawable.background_dashboard
                            AppScreen.Statistics -> R.drawable.background_stats
                            AppScreen.History -> R.drawable.background_history
                            AppScreen.Settings -> R.drawable.background_settings
                            else -> null
                        }
                        if (backgroundResId != null) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = backgroundResId),
                                contentDescription = "Background",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().alpha(0.35f)
                            )
                        }
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "screen_routing"
                        ) { screen ->
                            when (screen) {
                                AppScreen.Dashboard -> DashboardScreen(viewModel = viewModel)
                                AppScreen.UpdateGems -> UpdateGemsScreen(viewModel = viewModel)
                                AppScreen.Statistics -> StatisticsScreen(viewModel = viewModel)
                                AppScreen.History -> HistoryScreen(viewModel = viewModel)
                                AppScreen.Target -> TargetScreen(viewModel = viewModel)
                                AppScreen.OfflineAI -> OfflineAIScreen(viewModel = viewModel)
                                AppScreen.Settings -> SettingsScreen(viewModel = viewModel)
                                AppScreen.About -> AboutScreen(viewModel = viewModel)
                                AppScreen.Partner -> PartnerDesaScreen(viewModel = viewModel)
                                else -> {}
                            }
                        }
                    }
                }

                // If username is empty string, block main activity behind entry dialog cards!
                if (username.isBlank() && currentScreen != AppScreen.Splash) {
                    UsernameDialog(onSave = { viewModel.saveUsername(it) })
                }
            }
        }

        // Active automatic achievement popups!
        activeAchievement?.let { milestone ->
            AchievementPopup(
                milestone = milestone,
                onDismiss = { viewModel.dismissAchievement() }
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// SPLASH SCREEN COMPOSABLE
// -----------------------------------------------------------------------------------
@Composable
fun SplashScreen() {
    var animStart by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animStart) 1.1f else 0.8f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (animStart) 1f else 0f,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "logo_alpha"
    )

    LaunchedEffect(Unit) {
        animStart = true
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer gold loading rings
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 3.dp
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "Logo Pedang & Api",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "COC F2P GEMS TRACKER",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(alpha)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mencatat perolehan Gems F2P offline",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(30.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFF332D27)
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// USERNAME REGISTRATION PROMPT DIALOG
// -----------------------------------------------------------------------------------
@Composable
fun UsernameDialog(onSave: (String) -> Unit) {
    var rawText by remember { mutableStateOf("") }
    var errText by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = {}, // Force fill username, mandatory field
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        NeonCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Selamat Datang, Chief!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mohon masukkan username Clash of Clans Anda untuk mulai mengamati perolehan Gems F2P.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = rawText,
                    onValueChange = {
                        rawText = it
                        if (it.isNotBlank()) errText = null
                    },
                    label = { Text("Username Chief") },
                    placeholder = { Text("Contoh: Maskaav") },
                    singleLine = true,
                    isError = errText != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errText != null) {
                    Text(
                        text = errText ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (rawText.trim().isBlank()) {
                            errText = "Username wajib diisi!"
                        } else {
                            onSave(rawText.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_username_button")
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Masuk ke Desa", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// DASHBOARD SCREEN COMPOSABLE
// -----------------------------------------------------------------------------------
@Composable
fun DashboardScreen(viewModel: GemsViewModel) {
    val username by viewModel.username.collectAsState()
    val totalToday by viewModel.totalGemsToday.collectAsState()
    val totalThisWeek by viewModel.totalGemsThisWeek.collectAsState()
    val totalThisMonth by viewModel.totalGemsThisMonth.collectAsState()
    val totalOverall by viewModel.totalGemsOverall.collectAsState()
    val targetGems by viewModel.targetGems.collectAsState()
    
    val activeProfileId by viewModel.activeProfileId.collectAsState()
    val profileUsernames by viewModel.profileUsernames.collectAsState()

    val progress = if (targetGems > 0) {
        (totalOverall.toFloat() / targetGems.toFloat()).coerceIn(0f, 1f)
    } else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "COC F2P Gems Tracker",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )
        }
        // Profile Selector
        item {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(5) { index ->
                    val profileId = index + 1
                    val pName = profileUsernames.getOrNull(index)?.takeIf { it.isNotBlank() } ?: "Acc $profileId"
                    val isSelected = activeProfileId == profileId
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2D1F16),
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.clickable { viewModel.setActiveProfile(profileId) }
                    ) {
                        Text(
                            text = pName,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 1. Welcome Header Section (Vibrant Palette style)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "WELCOME BACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp,
                        modifier = Modifier.alpha(0.8f)
                    )
                    Text(
                        text = username.ifBlank { "Chief" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Rounded gradient icon container representing Crossed Swords / Emblem
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFEA580C), Color(0xFFFFB600))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Desa Logo",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // 2. Total Gems Card (Vibrant Card containing radial glow & emerald confirmation check)
        item {
            val txs by viewModel.transactions.collectAsState()
            NeonCard(
                shape = RoundedCornerShape(24.dp), // rounded-3xl
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp)),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Soft orange blur glow in the upper right
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 16.dp, y = (-16).dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                               text = "Total Gems Overall",
                               fontSize = 13.sp,
                               fontWeight = FontWeight.Medium,
                               color = Color(0xFF94A3B8) // slate-400
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                               verticalAlignment = Alignment.CenterVertically,
                               horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                   text = String.format("%,d", totalOverall),
                                   fontSize = 36.sp,
                                   fontWeight = FontWeight.Black,
                                   color = Color.White,
                                   letterSpacing = (-1).sp
                                )

                                // Small emerald confirmation check bubble
                                Box(
                                   modifier = Modifier
                                       .size(20.dp)
                                       .background(Color(0xFF10B981), CircleShape),
                                   contentAlignment = Alignment.Center
                                ) {
                                   Icon(
                                       imageVector = Icons.Default.Check,
                                       contentDescription = null,
                                       tint = Color.White,
                                       modifier = Modifier.size(12.dp)
                                   )
                                }
                            }
                        }

                        // Growth percentage or milestone pill
                        Box(
                            modifier = Modifier
                               .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                               .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                               text = "+F2P",
                               fontSize = 11.sp,
                               fontWeight = FontWeight.Bold,
                               color = Color(0xFF34D399) // emerald-400
                            )
                        }
                    }
                }
            }
        }

        // 3. Grid Stats (Double column containers of MaterialTheme.colorScheme.surface)
        item {
            val txs by viewModel.transactions.collectAsState()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Col 1: Today
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "TODAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$totalToday Gems",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFB923C) // orange-400
                            )
                        }
                    }

                    // Col 2: This Week
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "THIS WEEK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$totalThisWeek Gems",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Col 1: Input Gems
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "INPUT GEMS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${txs.size} Total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Col 2: Logs
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "HISTORY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${txs.filter { it.gems > 0 }.size} Logs",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 4. Target Progress (Orange/Gold linear bar with custom italic summaries)
        item {
            val remaining = (targetGems - totalOverall).coerceAtLeast(0)
            NeonCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target: Gems Impian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = String.format("%,d / %,d", totalOverall, targetGems),
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8) // slate-400
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Premium linear overflow bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.05f)) // bg-white/5
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFEA580C), // orange-600
                                            MaterialTheme.colorScheme.primary, // orange-500
                                            Color(0xFFFFD700)  // yellow-gold
                                        )
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val percent = (progress * 100).toInt()
                    Text(
                        text = "$percent% Terpenuhi • $remaining Gems tersisa",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8), // slate-400
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 5. AI Insight Offline/Online Calculator Card
        item {
            val txs by viewModel.transactions.collectAsState()
            val aiMode by viewModel.aiMode.collectAsState()
            val onlineInsight by viewModel.onlineAiInsight.collectAsState()
            val isFetchingAi by viewModel.isFetchingAi.collectAsState()
            
            val activeDays = remember(txs) {
                if (txs.isEmpty()) 1 else {
                    val firstTime = txs.minOf { it.timestamp }
                    val diffMs = System.currentTimeMillis() - firstTime
                    val days = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0) + 1
                    days.toInt()
                }
            }
            val averageGemsPerDay = if (txs.isEmpty()) 0.0 else totalOverall.toDouble() / activeDays.toDouble()
            val remainingGems = (targetGems - totalOverall).coerceAtLeast(0)
            val estimasiHari = if (remainingGems == 0) 0 else if (averageGemsPerDay <= 0.0) -1 else ceil(remainingGems.toDouble() / averageGemsPerDay).toInt()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2D1F16), MaterialTheme.colorScheme.background)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color(0xFFFB923C), // orange-400
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        if (aiMode == "online") {
                            Text(
                                text = "AI Online Insight:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (onlineInsight != null) {
                                Text(
                                    text = onlineInsight!!,
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 16.sp
                                )
                            } else {
                                Text(
                                    text = "Klik tombol di bawah untuk mendapatkan motivasi dan estimasi AI.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchOnlineAiInsight(targetGems, totalOverall, averageGemsPerDay) },
                                enabled = !isFetchingAi,
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isFetchingAi) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Minta Insight AI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            val textInsight = if (remainingGems == 0) {
                                "Luar biasa, Chief! Target Anda saat ini telah tuntas tercapai secara penuh."
                            } else if (estimasiHari <= 0) {
                                "AI Offline Insight: Masukkan data Gems pertama Anda agar AI dapat memproyeksikan perkiraan hari pencapaian."
                            } else {
                                "AI Offline Insight: Berdasarkan rata-rata harian Anda sebesar ${String.format("%.1f", averageGemsPerDay)} Gems/hari, Anda akan mencapai target dalam $estimasiHari hari."
                            }
                            Text(
                                text = textInsight,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1), // slate-300
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 6. Subgrid navigation menu for quick feature jumps
        item {
            NeonCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Menu Navigasi Fitur",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MenuButton(
                            title = "Update Gems",
                            icon = Icons.Default.AddCircle,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { viewModel.setScreen(AppScreen.UpdateGems) },
                            modifier = Modifier.weight(1f)
                        )
                        MenuButton(
                            title = "Statistik",
                            icon = Icons.Default.BarChart,
                            color = MaterialTheme.colorScheme.secondary,
                            onClick = { viewModel.setScreen(AppScreen.Statistics) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MenuButton(
                            title = "Riwayat",
                            icon = Icons.Default.History,
                            color = Color(0xFF60A5FA),
                            onClick = { viewModel.setScreen(AppScreen.History) },
                            modifier = Modifier.weight(1f)
                        )
                        MenuButton(
                            title = "Target Gems",
                            icon = Icons.Default.TrackChanges,
                            color = Color(0xFFF43F5E),
                            onClick = { viewModel.setScreen(AppScreen.Target) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MenuButton(
                            title = "Analisa AI",
                            icon = Icons.Default.SmartToy,
                            color = MaterialTheme.colorScheme.tertiary,
                            onClick = { viewModel.setScreen(AppScreen.OfflineAI) },
                            modifier = Modifier.weight(1f)
                        )
                        MenuButton(
                            title = "Settings",
                            icon = Icons.Default.Settings,
                            color = Color.LightGray,
                            onClick = { viewModel.setScreen(AppScreen.Settings) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MenuButton(
                            title = "Partner Desa",
                            icon = Icons.Default.ChatBubble,
                            color = Color(0xFFF59E0B),
                            onClick = { viewModel.setScreen(AppScreen.Partner) },
                            modifier = Modifier.weight(1f)
                        )
                        MenuButton(
                            title = "Tentang Aplikasi",
                            icon = Icons.Default.Info,
                            color = Color(0xFFA78BFA),
                            onClick = { viewModel.setScreen(AppScreen.About) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    NeonCard(
                    shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun MenuButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeonCard(
                    shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------
// UPDATE GEMS SCREEN (INPUT GEMS FORM)
// -----------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdateGemsScreen(viewModel: GemsViewModel) {
    var rawGems by remember { mutableStateOf("") }
    var rawNote by remember { mutableStateOf("") }
    var selectedSource by remember { mutableStateOf("Pohon") }
    var errGems by remember { mutableStateOf<String?>(null) }

    val sources = listOf(
        "Pohon", "Batu", "Rumput", "Gem Box",
        "Penjualan Kitab", "Penjualan Mantra", "Misi", "Lainnya"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Required header style as Emerald Green in compliance with the color instructions
        Text(
            text = "Input Gems",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )

        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Gems amount input
                OutlinedTextField(
                    value = rawGems,
                    onValueChange = {
                        rawGems = it
                        if (it.toIntOrNull() != null && it.toInt() > 0) {
                            errGems = null
                        }
                    },
                    label = { Text("Jumlah Gems Baru") },
                    placeholder = { Text("Contoh: 25") },
                    singleLine = true,
                    isError = errGems != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gems_input_field"),
                    shape = RoundedCornerShape(12.dp)
                )
                errGems?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Source selector
                Text(
                    text = "Sumber Gems",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render grid flow layout for select chips in Indonesian
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sources.forEach { src ->
                        val isSelected = selectedSource == src
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSource = src },
                            label = { Text(src) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Optional note field
                OutlinedTextField(
                    value = rawNote,
                    onValueChange = { rawNote = it },
                    label = { Text("Catatan Opsional") },
                    placeholder = { Text("Ketik catatan di sini...") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_input_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val amount = rawGems.trim().toIntOrNull()
                        if (amount == null || amount <= 0) {
                            errGems = "Gems harus berupa angka bulat positif!"
                        } else {
                            viewModel.addTransaction(amount, selectedSource, rawNote.trim())
                            viewModel.setScreen(AppScreen.Dashboard)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_gems_button")
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Gems", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// STATISTICS SCREEN COMPOSABLE (WITH PLOTTED GRAPHS)
// -----------------------------------------------------------------------------------
@Composable
fun StatisticsScreen(viewModel: GemsViewModel) {
    val txs by viewModel.transactions.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Harian") } // Harian, Mingguan, Bulanan, Tahunan

    // Calculate details dynamically based on chosen period
    val periodGemsMap = remember(txs, selectedPeriod) {
        val map = TreeMap<String, Int>()
        val calendar = Calendar.getInstance()
        val format = when (selectedPeriod) {
            "Harian" -> SimpleDateFormat("dd MMM", Locale("in", "ID"))
            "Mingguan" -> SimpleDateFormat("'W'w, MMM", Locale("in", "ID"))
            "Bulanan" -> SimpleDateFormat("MMM yyyy", Locale("in", "ID"))
            else -> SimpleDateFormat("yyyy", Locale("in", "ID"))
        }

        // Fill mapping details
        txs.forEach { tx ->
            val date = Date(tx.timestamp)
            val key = format.format(date)
            map[key] = (map[key] ?: 0) + tx.gems
        }
        map.toList().takeLast(6) // Take the last 6 records of activity to draw
    }

    val totalGems = periodGemsMap.sumOf { it.second }
    val averageGems = if (periodGemsMap.isNotEmpty()) totalGems.toFloat() / periodGemsMap.size else 0f

    // Calculate growth factor: change from the previous node to help fulfill: "Tampilkan: Pertumbuhan, Rata-rata"
    val growthText = remember(periodGemsMap) {
        if (periodGemsMap.size >= 2) {
            val last = periodGemsMap.last().second
            val prev = periodGemsMap[periodGemsMap.size - 2].second
            val diff = last - prev
            if (diff >= 0) "+$diff dibanding periode sebelumnya" else "$diff dibanding periode sebelumnya"
        } else {
            "Data belum terkumpul cukup untuk melihat pertumbuhan."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Filter Periode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Harian", "Mingguan", "Bulanan", "Tahunan").forEach { period ->
                        val isSelected = selectedPeriod == period
                        Button(
                            onClick = { selectedPeriod = period },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(text = period, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Summary details panel
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Total Gems", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalGems", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                }
                Box(modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color.Gray.copy(alpha = 0.3f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Rata-rata", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(String.format("%.1f", averageGems), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Box(modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color.Gray.copy(alpha = 0.3f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Pertumbuhan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (periodGemsMap.size >= 2) {
                            val valDiff = periodGemsMap.last().second - periodGemsMap[periodGemsMap.size - 2].second
                            if (valDiff >= 0) "+$valDiff" else "$valDiff"
                        } else "N/A",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        // Beautiful Graphic Canvas Card
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Struktur Grafik Perolehan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (periodGemsMap.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada perolehan Gems pada periode ini.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Modern styled Custom Column Canvas Graph
                    val maxValue = (periodGemsMap.maxOf { it.second }.toFloat() * 1.25f).coerceAtLeast(10f)

                    val canvasPrimaryColor = MaterialTheme.colorScheme.primary
                    val canvasSecondaryColor = MaterialTheme.colorScheme.secondary
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height
                            val spacing = width / (periodGemsMap.size)
                            val chartBottom = height - 40f
                            val chartTop = 20f
                            val drawHeight = chartBottom - chartTop

                            // Draw subtle baseline and guides
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = androidx.compose.ui.geometry.Offset(0f, chartBottom),
                                end = androidx.compose.ui.geometry.Offset(width, chartBottom),
                                strokeWidth = 2f
                            )

                            // Render columns
                            periodGemsMap.forEachIndexed { idx, pair ->
                                val label = pair.first
                                val value = pair.second

                                val barHeight = (value.toFloat() / maxValue) * drawHeight
                                val barWidth = (spacing * 0.45f).coerceIn(16f, 80f)
                                val xPos = (idx * spacing) + (spacing / 2) - (barWidth / 2)
                                val yPos = chartBottom - barHeight

                                // Draw bar gradient
                                
                                
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(canvasPrimaryColor, canvasSecondaryColor)
                                    ),
                                    topLeft = androidx.compose.ui.geometry.Offset(xPos, yPos),
                                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                )

                                // Text drawing in Compose Canvas requires Native Paint or elegant overlaid absolute text values!
                                // To make code 100% stable, we can overlap the visual graphics with absolute Compose Column elements or draw native. Let's draw native circles to signify markers!
                                drawCircle(
                                    color = Color.White,
                                    radius = 3f,
                                    center = androidx.compose.ui.geometry.Offset(xPos + (barWidth / 2), yPos)
                                )
                            }
                        }

                        // Superimpose exact numerical tag text and label text nicely using absolute layout boxes below and above of Canvas
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            periodGemsMap.forEach { pair ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "+${pair.second}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 220.dp).offset(y = 175.dp)
                                    )
                                    Text(
                                        text = pair.first,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Growth summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = growthText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// RIWAYAT SCREEN COMPOSABLE (WITH SEARCH, FILTER, DELETE ACTIONS)
// -----------------------------------------------------------------------------------
@Composable
fun HistoryScreen(viewModel: GemsViewModel) {
    val txs by viewModel.filteredTransactions.collectAsState()
    val rawQuery by viewModel.searchQuery.collectAsState()
    val filterSrc by viewModel.filterSource.collectAsState()

    val sourcesList = listOf("Semua", "Pohon", "Batu", "Rumput", "Gem Box", "Penjualan Kitab", "Penjualan Mantra", "Misi", "Lainnya")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Required subtitle styling "Riwayat Gems" is Emerald Green in compliance with the prompt
        Text(
            text = "Riwayat Gems",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )

        // Find/Search outliner card
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rawQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Cari catatan/sumber...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Horizontal source scrolling filtering chips
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sourcesList.forEach { src ->
                            val isSelected = (src == "Semua" && filterSrc == null) || (filterSrc == src)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.filterSource.value = if (src == "Semua") null else src
                                },
                                label = { Text(src) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Transactions list view
        if (txs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Data riwayat kosong atau tidak ditemukan.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(txs, key = { it.id }) { tx ->
                    var isExpanded by remember { mutableStateOf(false) }
                    NeonCard(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Custom visual bubble for the source matching gamer style
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = when (tx.source) {
                                                    "Gem Box" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                                    "Pohon", "Rumput" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                    "Batu" -> Color.Gray.copy(alpha = 0.2f)
                                                    "Penjualan Kitab", "Penjualan Mantra" -> Color(0xFFA78BFA).copy(alpha = 0.2f)
                                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (tx.source) {
                                                "Gem Box" -> Icons.Default.CardGiftcard
                                                "Pohon", "Rumput" -> Icons.Default.FilterVintage
                                                "Batu" -> Icons.Default.Terrain
                                                "Penjualan Kitab" -> Icons.Default.AutoStories
                                                "Penjualan Mantra" -> Icons.Default.Science
                                                "Misi" -> Icons.Default.Flag
                                                else -> Icons.Default.AttachMoney
                                            },
                                            contentDescription = null,
                                            tint = when (tx.source) {
                                                "Gem Box" -> MaterialTheme.colorScheme.secondary
                                                "Pohon", "Rumput" -> MaterialTheme.colorScheme.tertiary
                                                "Batu" -> Color.Gray
                                                "Penjualan Kitab", "Penjualan Mantra" -> Color(0xFFA78BFA)
                                                else -> MaterialTheme.colorScheme.primary
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = tx.source,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val formDate = remember(tx.timestamp) {
                                            val date = Date(tx.timestamp)
                                            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
                                            sdf.format(date)
                                        }
                                        Text(
                                            text = formDate,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+${tx.gems}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (tx.note.isNotBlank() || isExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Catatan: ${if (tx.note.isNotBlank()) tx.note else '-'}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }

                            // Extra details layout if card clicked
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.Gray.copy(alpha = 0.2f))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { viewModel.deleteTransaction(tx.id) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Hapus Catatan", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// TARGET GEMS SCREEN COMPOSABLE
// -----------------------------------------------------------------------------------
@Composable
fun TargetScreen(viewModel: GemsViewModel) {
    val currentTarget by viewModel.targetGems.collectAsState()
    val totalOverall by viewModel.totalGemsOverall.collectAsState()

    var customInputVal by remember { mutableStateOf("") }
    var errTargetVal by remember { mutableStateOf<String?>(null) }

    val presents = listOf(100, 500, 1000, 5000, 10000)

    val remaining = (currentTarget - totalOverall).coerceAtLeast(0)
    val progress = if (currentTarget > 0) {
        (totalOverall.toFloat() / currentTarget.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Konfigurasi Target Gems",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Render preset chips
                Text(text = "Rekomendasi preset target:", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presents.take(3).forEach { p ->
                        val isSelected = currentTarget == p
                        Button(
                            onClick = {
                                viewModel.saveTargetGems(p)
                                customInputVal = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text("$p", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presents.drop(3).forEach { p ->
                        val isSelected = currentTarget == p
                        Button(
                            onClick = {
                                viewModel.saveTargetGems(p)
                                customInputVal = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text("$p", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom input card
                OutlinedTextField(
                    value = customInputVal,
                    onValueChange = {
                        customInputVal = it
                        if (it.toIntOrNull() != null && it.toInt() > 0) {
                            errTargetVal = null
                        }
                    },
                    label = { Text("Ubah target manual") },
                    placeholder = { Text("Contoh: 2500") },
                    singleLine = true,
                    isError = errTargetVal != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_gems_input_field"),
                    shape = RoundedCornerShape(12.dp)
                )
                errTargetVal?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val parsed = customInputVal.trim().toIntOrNull()
                        if (parsed == null || parsed <= 0) {
                            errTargetVal = "Masukkan nilai target angka positif bulat yang valid!"
                        } else {
                            viewModel.saveTargetGems(parsed)
                            customInputVal = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.Black)
                ) {
                    Text("Terapkan Target Baru", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active details progress
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Kemajuan Aktif Target",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% Tercapai",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Sisa: $remaining Gems",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                )

                Text(
                    text = if (remaining == 0) {
                        "Luar biasa, Chief! Target Anda saat ini telah tuntas tercapai secara penuh. Silakan naikkan target Anda!"
                    } else {
                        "Anda memerlukan $remaining Gems lagi untuk menyelesaikan tantangan pengumpulan target $currentTarget Gems saat ini. Semangat menambang, Chief!"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// OFFLINE AI ANALYSIS SCREEN (HEURISTIC STATS CALCULATIONS)
// -----------------------------------------------------------------------------------
@Composable
fun OfflineAIScreen(viewModel: GemsViewModel) {
    val txs by viewModel.transactions.collectAsState()
    val currentTarget by viewModel.targetGems.collectAsState()
    val totalOverall by viewModel.totalGemsOverall.collectAsState()

    // Calculate active days since starting
    val activeDays = remember(txs) {
        if (txs.isEmpty()) 1 else {
            val firstTime = txs.minOf { it.timestamp }
            val diffMs = System.currentTimeMillis() - firstTime
            val days = (diffMs / (1000 * 60 * 60 * 24)).coerceAtLeast(0) + 1
            days.toInt()
        }
    }

    val averageGemsPerDay = if (txs.isEmpty()) 0.0 else totalOverall.toDouble() / activeDays.toDouble()

    // Days remaining projection
    val remainingGems = (currentTarget - totalOverall).coerceAtLeast(0)
    val estimasiHari = remember(averageGemsPerDay, remainingGems) {
        if (remainingGems == 0) 0
        else if (averageGemsPerDay <= 0.0) -1
        else ceil(remainingGems.toDouble() / averageGemsPerDay).toInt()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Analytics, "AI Analisa", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Metrik Analisa F2P Desa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.2f)))

                MetricRow(label = "Rata-rata Gems per Hari", value = String.format("%.2f Gems/hari", averageGemsPerDay), color = MaterialTheme.colorScheme.primary)
                MetricRow(label = "Batas Mulai Tambang (Hari Aktif)", value = "$activeDays hari", color = MaterialTheme.colorScheme.secondary)
                MetricRow(label = "Sisa Proyeksi Target", value = "$remainingGems Gems", color = Color(0xFF60A5FA))
            }
        }

        // Proyeksi & Prediksi Card
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Proyeksi Pencapaian Target",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Render dynamic projection prediction tags
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Estimasi Hari Menuju Target:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        if (remainingGems == 0) {
                            Text(
                                text = "TARGET SUDAH TERCAPAI!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        } else if (estimasiHari <= 0) {
                            Text(
                                text = "Diperlukan data pengumpulan...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "~$estimasiHari Hari ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "lagi",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Friendly advice
                val predictionAdvice = remember(averageGemsPerDay, remainingGems) {
                    if (remainingGems == 0) {
                        "Target Anda saat ini telah terpenuhi, silahkan pilih target impian di tab Target Gems untuk memulai target baru!"
                    } else if (averageGemsPerDay <= 0.0) {
                        "Catatan perolehan Anda masih kosong atau bernilai nol. Silahkan masukkan data Gems pertama Anda di menu Update Gems."
                    } else {
                        "Jika rata-rata tetap " + String.format("%.1f", averageGemsPerDay) + " Gems per hari, target Anda akan tercapai dalam " + estimasiHari + " hari."
                    }
                }

                Text(
                    text = predictionAdvice,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }

        // Tren / Trend details card
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tren Speed Pengumpulan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                val trendText = remember(txs) {
                    if (txs.size < 3) {
                        "Butuh setidaknya 3 entri berbeda untuk membaca diagram tren perolehan Anda saat ini."
                    } else {
                        val lastGems = txs.take(3).sumOf { it.gems }
                        val speedOfCollect = lastGems / 3
                        if (speedOfCollect >= 30) {
                            "Kecepatan Luar Biasa! Pengumpulan Gems Anda dalam beberapa hari terakhir sangat aktif ($speedOfCollect Gems/entri). Pertahankan momentum ini!"
                        } else if (speedOfCollect >= 15) {
                            "Kecepatan Stabil. Anda tertib mengumpulkan rata-rata $speedOfCollect Gems per entri. Bagus, Chief!"
                        } else {
                            "Kecepatan Ringan. Pengumpulan Gems rata-rata $speedOfCollect per entri. Carilah sesering mungkin Gem Box atau rintangan di desa Anda."
                        }
                    }
                }

                Text(
                    text = trendText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
    }
}

// -----------------------------------------------------------------------------------
// SETTINGS SCREEN COMPOSABLE (WITH OFFLINE BACKUP COPIER & RESTORER)
// -----------------------------------------------------------------------------------
@Composable
fun SettingsScreen(viewModel: GemsViewModel) {
    val username by viewModel.username.collectAsState()
    val isDarkOverriden by viewModel.darkTheme.collectAsState()
    val appThemeState by viewModel.appTheme.collectAsState()
    val currentContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    
    val createDocumentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    val code = viewModel.generateBackup()
                    currentContext.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(code.toByteArray())
                    }
                    Toast.makeText(currentContext, "Backup berhasil disimpan!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    val openDocumentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        val code = currentContext.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                            reader.readText()
                        }
                        if (code != null) {
                            viewModel.restoreBackup(code)
                            Toast.makeText(currentContext, "Data berhasil dipulihkan!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(currentContext, "Gagal membaca file backup", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }

    var showResetTodayPrompt by remember { mutableStateOf(false) }
    var showResetAllPrompt by remember { mutableStateOf(false) }

    var restoreBackupInput by remember { mutableStateOf("") }
    var showRestoreHelp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Akun Profil & Nama",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Username Chief", fontSize = 13.sp)
                        Text(text = username, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = {
                            editNameInput = username
                            showEditNameDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text("Ganti", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Manual theme overrides matching dark/light mode configurations
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Konfigurasi Tema Visual",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                var showConfirmRestartDialog by remember { mutableStateOf<String?>(null) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { if (appThemeState != "default") showConfirmRestartDialog = "default" },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appThemeState == "default") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            contentColor = if (appThemeState == "default") Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Default", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { if (appThemeState != "electro") showConfirmRestartDialog = "electro" },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appThemeState == "electro") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            contentColor = if (appThemeState == "electro") Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Electro Dragon", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                if (showConfirmRestartDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showConfirmRestartDialog = null },
                        title = { Text("Ganti Tema") },
                        text = { Text("Aplikasi akan dimuat ulang untuk menerapkan tema. Lanjutkan?") },
                        confirmButton = {
                            TextButton(onClick = { 
                                val targetTheme = showConfirmRestartDialog!!
                                scope.launch {
                                    viewModel.saveAppThemeSync(targetTheme)
                                    showConfirmRestartDialog = null
                                    
                                    // Restart
                                    val intent = currentContext.packageManager.getLaunchIntentForPackage(currentContext.packageName)
                                    val componentName = intent?.component
                                    val mainIntent = android.content.Intent.makeRestartActivityTask(componentName)
                                    currentContext.startActivity(mainIntent)
                                    Runtime.getRuntime().exit(0)
                                }
                            }) {
                                Text("Iya")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmRestartDialog = null }) {
                                Text("Tidak")
                            }
                        }
                    )
                }
            }
        }

        // AI Provider Configuration Card
        val aiMode by viewModel.aiMode.collectAsState()
        val aiProvider by viewModel.aiProvider.collectAsState()
        val geminiApiKey by viewModel.geminiApiKey.collectAsState()
        val groqApiKey by viewModel.groqApiKey.collectAsState()
        
        var editGeminiKey by remember(geminiApiKey) { mutableStateOf(geminiApiKey) }
        var editGroqKey by remember(groqApiKey) { mutableStateOf(groqApiKey) }

        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Konfigurasi Insight AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveAiMode("offline") },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (aiMode == "offline") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            contentColor = if (aiMode == "offline") Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("AI Offline", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.saveAiMode("online") },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (aiMode == "online") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            contentColor = if (aiMode == "online") Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("AI Online", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (aiMode == "online") {
                    Text(
                        text = "Pilih penyedia API yang akan digunakan untuk Online Insight:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveAiProvider("gemini") },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (aiProvider == "gemini") Color(0xFF60A5FA) else MaterialTheme.colorScheme.background,
                                contentColor = if (aiProvider == "gemini") Color.White else MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Gemini", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.saveAiProvider("groq") },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (aiProvider == "groq") Color(0xFF10B981) else MaterialTheme.colorScheme.background,
                                contentColor = if (aiProvider == "groq") Color.White else MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Groq", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = if (aiProvider == "gemini") editGeminiKey else editGroqKey,
                        onValueChange = { 
                            if (aiProvider == "gemini") editGeminiKey = it else editGroqKey = it 
                        },
                        label = { Text("API Key ${if (aiProvider == "gemini") "Gemini" else "Groq"}") },
                        placeholder = { Text("Masukkan API Key Anda") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Button(
                        onClick = { 
                            if (aiProvider == "gemini") viewModel.saveGeminiApiKey(editGeminiKey) 
                            else viewModel.saveGroqApiKey(editGroqKey)
                            Toast.makeText(currentContext, "API Key disimpan!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simpan API Key", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Backup & Restore Area Card (Works 100% offline via safe Base64 clipboards strings copy paste)
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Backup & Restore Data Lokal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    text = "Simpan data Chief secara aman secara manual kapan pun Anda butuhkan. Menyalin data cadangan teks Anda ke catatan internal dan masukkan kembali kapan saja untuk dipulihkan.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.61f),
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                            val dateString = dateFormat.format(java.util.Date())
                            createDocumentLauncher.launch("coc_backup_${dateString}.json")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("backup_copy_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Backup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { openDocumentLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pilih Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dangerous Zones Card
        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Area Bahaya (Reset)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Red
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Reset today only button
                    Button(
                        onClick = { showResetTodayPrompt = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_today_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Reset Hari Ini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Reset overall database button
                    Button(
                        onClick = { showResetAllPrompt = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_all_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Reset Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Edit profile username Dialog popup
        if (showEditNameDialog) {
            Dialog(onDismissRequest = { showEditNameDialog = false }) {
                NeonCard(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Ubah Username", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        OutlinedTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            placeholder = { Text("Contoh: Maskaav") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showEditNameDialog = false }) { Text("Batal") }
                            TextButton(
                                onClick = {
                                    if (editNameInput.isNotBlank()) {
                                        viewModel.saveUsername(editNameInput.trim())
                                        showEditNameDialog = false
                                    }
                                }
                            ) { Text("Simpan") }
                        }
                    }
                }
            }
        }

        // Confirm Today resets Dialog
        if (showResetTodayPrompt) {
            Dialog(onDismissRequest = { showResetTodayPrompt = false }) {
                NeonCard(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Konfirmasi Reset Hari Ini", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Red)
                        Text("Apakah Anda yakin ingin menghapus semua catatan Gems yang terdaftar pada hari ini saja?", fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showResetTodayPrompt = false }) { Text("Batal") }
                            TextButton(
                                onClick = {
                                    viewModel.resetToday()
                                    showResetTodayPrompt = false
                                }
                            ) { Text("Ya, Reset", color = Color.Red) }
                        }
                    }
                }
            }
        }

        // Confirm heavy resets Dialog
        if (showResetAllPrompt) {
            Dialog(onDismissRequest = { showResetAllPrompt = false }) {
                NeonCard(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("KONFIRMASI RESET SEUTUHNYA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Red)
                        Text("Seluruh data profil, target, dan riwayat Gems akan dihapus permanen. Tindakan ini TIDAK dapat dibatalkan!", fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showResetAllPrompt = false }) { Text("Batal") }
                            TextButton(
                                onClick = {
                                    viewModel.resetAllData()
                                    showResetAllPrompt = false
                                }
                            ) { Text("Hapus Semua Data", color = Color.Red) }
                        }
                    }
                }
            }
        }

        // Restore Help dialog pasting textbox
        if (showRestoreHelp) {
            Dialog(onDismissRequest = { showRestoreHelp = false }) {
                NeonCard(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Restore Data Cadangan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("Tempel kode Base64 backup Anda di bawah ini dan tekan pulihkan:", fontSize = 12.sp)

                        OutlinedTextField(
                            value = restoreBackupInput,
                            onValueChange = { restoreBackupInput = it },
                            placeholder = { Text("Tempel teks di sini...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("restore_paste_button"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                restoreBackupInput = ""
                                showRestoreHelp = false
                            }) { Text("Batal") }
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        val parseSuccess = viewModel.restoreBackup(restoreBackupInput)
                                        if (parseSuccess) {
                                            restoreBackupInput = ""
                                            showRestoreHelp = false
                                        }
                                    }
                                }
                            ) { Text("Pulihkan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// TENTANG APLIKASI SCREEN COMPOSABLE
// -----------------------------------------------------------------------------------
@Composable
fun AboutScreen(viewModel: GemsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "COC F2P Gems Tracker",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "Versi 1.0.0",
            fontSize = 12.sp,
            color = Color.Gray
        )

        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Informasi Pengembang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.2f)))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Developer", fontSize = 13.sp)
                    Text("Maskaav", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Platform", fontSize = 13.sp)
                    Text("Android Native (Kotlin)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pustaka Basis", fontSize = 13.sp)
                    Text("Jetpack Compose & Room", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        NeonCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Deskripsi Aplikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "Aplikasi ini digunakan untuk mencatat dan memantau perolehan Gems pemain Clash of Clans gratisan (F2P) tanpa menggunakan top up. Desain dibuat sepenuhnya berjalan 100% secara offline mandiri tanpa membutuhkan API pihak ketiga demi kerahasiaan keamanan penuh desa Anda.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.77f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------
// ACHIEVEMENT POPUP SCREEN (WITH CONFETTI ANIMATIONS particle generator PHYSICS)
// -----------------------------------------------------------------------------------
@Composable
fun AchievementPopup(milestone: Int, onDismiss: () -> Unit) {
    // Basic dynamic multi-color particle physics for purely native lightweight confetti
    val particlesCount = 45

    val primaryConfetti = MaterialTheme.colorScheme.primary
    val secondaryConfetti = MaterialTheme.colorScheme.secondary
    val tertiaryConfetti = MaterialTheme.colorScheme.tertiary
    
    val particlesList = remember {
        List(particlesCount) {
            val ranX = Random.nextFloat()
            val ranSpeedY = Random.nextFloat() * 12f + 6f
            val ranColor = when (Random.nextInt(4)) {
                0 -> secondaryConfetti
                1 -> primaryConfetti
                2 -> tertiaryConfetti
                else -> Color(0xFF60A5FA)
            }

            ConfettiParticle(xOffsetFraction = ranX, speedY = ranSpeedY, color = ranColor)
        }
    }

    var tickState by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // tick roughly 60fps loops
            particlesList.forEach { p ->
                p.currentY += p.speedY
                if (p.currentY > 1500) { // Recycle back up
                    p.currentY = -30f
                }
            }
            tickState++
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            // Draw interactive confetti loops behind popup
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cw = size.width
                particlesList.forEach { p ->
                    val x = p.xOffsetFraction * cw
                    drawCircle(
                        color = p.color,
                        radius = 8f,
                        center = androidx.compose.ui.geometry.Offset(x, p.currentY)
                    )
                }
            }

            // Central congratulations achievement Card
            NeonCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.secondary), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.radialGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "Penghargaan Terbuka!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Selamat, Chief! Total tabungan Gems Anda telah berhasil melampaui milestone $milestone Gems!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
                    ) {
                        Text("Terima Kasih, Lanjutkan!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerDesaScreen(viewModel: GemsViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // Chat Area
        androidx.compose.foundation.lazy.LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { message ->
                val isUser = message.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(
                                color = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface,
                                shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                                        else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isUser) Color(0xFFFB923C) else Color(0xFF10B981).copy(alpha = 0.5f),
                                shape = if (isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                                        else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = message.content,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Capsules
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickActions = listOf("Edukasi Chief", "Prioritas Upgrade", "Formasi Base", "Saran Pasukan", "Tips Hero")
            items(quickActions) { action ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable {
                        viewModel.sendChatMessage("Berikan saran mengenai $action")
                    }
                ) {
                    Text(
                        text = action,
                        color = Color(0xFFFB923C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya Partner Desa...", color = Color.Gray, fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                maxLines = 1,
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFFEA580C), MaterialTheme.colorScheme.primary)),
                        shape = CircleShape
                    )
                    .clickable {
                        if (inputText.isNotBlank() && !isChatLoading) {
                            viewModel.sendChatMessage(inputText)
                            inputText = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Particle holder for our custom confetti loop
class ConfettiParticle(
    val xOffsetFraction: Float,
    val speedY: Float,
    val color: Color,
    var currentY: Float = -20f
)



fun Modifier.animatedNeonBorder(
    isElectro: Boolean,
    primaryColor: androidx.compose.ui.graphics.Color,
    secondaryColor: androidx.compose.ui.graphics.Color,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp
): Modifier = composed {
    if (!isElectro) return@composed this

    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "neon_rotation")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "neon_offset"
    )

    this.then(
        androidx.compose.ui.Modifier.drawWithContent {
            drawContent()
            val strokeWidthPx = borderWidth.toPx()
            val radiusPx = cornerRadius.toPx()
            
            val startX = if (offsetProgress < 0.5f) offsetProgress * 2 * size.width else (1f - (offsetProgress - 0.5f) * 2) * size.width
            val startY = if (offsetProgress < 0.5f) 0f else size.height
            val endX = size.width - startX
            val endY = size.height - startY
            
            val brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(primaryColor, secondaryColor, primaryColor),
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY)
            )

            drawRoundRect(
                brush = brush,
                topLeft = androidx.compose.ui.geometry.Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = androidx.compose.ui.geometry.Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radiusPx, radiusPx),
                style = Stroke(strokeWidthPx)
            )
        }
    )
}
