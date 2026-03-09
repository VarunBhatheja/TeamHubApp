package com.example.teamhubapp.feature_users.presentation.detail

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.presentation.components.user.AnimatedProfileImage
import com.example.teamhubapp.ui.theme.headerGradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId      : String,
    onBackClick : () -> Unit,
    viewModel   : UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark  = isSystemInDarkTheme()

    LaunchedEffect(userId) { viewModel.loadUser(userId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text          = "Employee Details",
                        fontSize      = 20.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = headerGradientColors(isDark).first()
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = headerGradientColors(isDark)))
        ) {
            when (val state = uiState) {

                is UserDetailUiState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                is UserDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text      = state.message,
                                color     = Color.White,
                                fontSize  = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(onClick = onBackClick) { Text("Go Back") }
                        }
                    }
                }

                is UserDetailUiState.Success -> {
                    UserDetailContent(
                        user          = state.user,
                        paddingValues = paddingValues,
                        isDark        = isDark
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailContent(
    user          : User,
    paddingValues : PaddingValues,
    isDark        : Boolean
) {
    val scrollState  = rememberScrollState()
    val density      = LocalDensity.current
    val heroHeightPx = with(density) { 240.dp.toPx() }

    // 0f = expanded hero, 1f = fully collapsed
    val collapseProgress  = (scrollState.value / heroHeightPx).coerceIn(0f, 1f)
    val expandedAlpha     = (1f - collapseProgress * 2f).coerceIn(0f, 1f)
    val collapsedAlpha    = ((collapseProgress - 0.3f) / 0.5f).coerceIn(0f, 1f)

    val avatarSize by animateDpAsState(
        targetValue   = lerp(110.dp, 38.dp, collapseProgress),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "avatarSize"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(paddingValues)
    ) {
        // Hero zone
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Expanded: big centered avatar + name (fades out on scroll)
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = expandedAlpha },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier         = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedProfileImage(
                        imageUrl = user.imageUrl,
                        userName = user.name,
                        isActive = user.isActive,
                        size     = 110
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text          = user.name,
                        fontSize      = 26.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    StatusPill(isActive = user.isActive)
                }
            }

            // Collapsed: small avatar + name row (fades in on scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp, start = 16.dp)
                    .graphicsLayer { alpha = collapsedAlpha },
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedProfileImage(
                        imageUrl = user.imageUrl,
                        userName = user.name,
                        isActive = user.isActive,
                        size     = 38
                    )
                }
                Text(
                    text          = user.name,
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = (-0.3).sp
                )
                StatusPill(isActive = user.isActive)
            }
        }

        // White card tray
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text          = "PROFILE INFO",
                    fontSize      = 15.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(Icons.Default.Person,       "DESIGNATION", user.designation)
                RowDivider()

                if (user.department.isNotBlank()) {
                    InfoRow(Icons.Default.Work,     "DEPARTMENT",  user.department)
                    RowDivider()
                }

                if (user.email.isNotBlank()) {
                    InfoRow(Icons.Default.Email,    "EMAIL",       user.email)
                    RowDivider()
                }

                InfoRow(Icons.Default.LocationOn,   "LOCATION",    "${user.city}, ${user.country}")

                if (user.joiningDate.isNotBlank()) {
                    RowDivider()
                    InfoRow(Icons.Default.CalendarMonth, "JOINED", user.joiningDate)
                }
            }
        }
    }
}

@Composable
private fun StatusPill(isActive: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) Color(0xFF34C759).copy(alpha = 0.20f)
                else          Color(0xFFFF3B30).copy(alpha = 0.20f)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color(0xFF34C759) else Color(0xFFFF3B30))
            )
            Text(
                text       = if (isActive) "Active" else "Inactive",
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = if (isActive) Color(0xFF34C759) else Color(0xFFFF3B30)
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon  : ImageVector,
    label : String,
    value : String
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text          = label,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text       = value,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        modifier = Modifier.padding(start = 56.dp)
    )
}