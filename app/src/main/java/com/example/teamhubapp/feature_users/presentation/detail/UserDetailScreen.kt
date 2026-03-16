package com.example.teamhubapp.feature_users.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.presentation.components.user.AnimatedProfileImage
import com.example.teamhubapp.ui.theme.headerGradientColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UserDetailScreen(
    userId                  : String,
    onBackClick             : () -> Unit,
    sharedTransitionScope   : SharedTransitionScope,
    animatedVisibilityScope : AnimatedVisibilityScope,
    viewModel               : UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark  = isSystemInDarkTheme()

    LaunchedEffect(userId) { viewModel.loadUser(userId) }

    when (val state = uiState) {
        is UserDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UserDetailUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message)
            }
        }
        is UserDetailUiState.Success -> {
            UserDetailContent(
                user                    = state.user,
                onBackClick             = onBackClick,
                isDark                  = isDark,
                sharedTransitionScope   = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun UserDetailContent(
    user                    : User,
    onBackClick             : () -> Unit,
    isDark                  : Boolean,
    sharedTransitionScope   : SharedTransitionScope,
    animatedVisibilityScope : AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val density     = LocalDensity.current

    val expandedHeroHeight  = 200.dp
    val collapsedHeroHeight = 72.dp

    Scaffold(
        topBar = {
            // ── FIXED TOP BAR ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(headerGradientColors(isDark)))
                    .statusBarsPadding()
                    .height(56.dp)
            ) {
                IconButton(
                    onClick  = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White
                    )
                }

                Text(
                    text       = "Employee Details",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    modifier   = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(padding)
                .fillMaxSize()
                .background(Brush.verticalGradient(headerGradientColors(isDark)))

        ) {
            val screenWidth = maxWidth

            val maxScroll = with(density) { (expandedHeroHeight - collapsedHeroHeight).toPx() }
            val scrollProgress = (scrollState.value / maxScroll).coerceIn(0f, 1f)

            val currentHeroHeight = lerp(expandedHeroHeight, collapsedHeroHeight, scrollProgress)

            // 1. SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(expandedHeroHeight))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text          = "PROFILE INFO",
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(Icons.Default.Person,     "DESIGNATION", user.designation)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    if (user.department.isNotBlank()) {
                        InfoRow(Icons.Default.Work,   "DEPARTMENT",  user.department)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }

                    if (user.email.isNotBlank()) {
                        InfoRow(Icons.Default.Email,  "EMAIL",       user.email)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }

                    InfoRow(Icons.Default.LocationOn, "LOCATION", "${user.city}, ${user.country}")

                    if (user.joiningDate.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        InfoRow(Icons.Default.CalendarMonth, "JOINED", user.joiningDate)
                    }

                    Spacer(modifier = Modifier.height(400.dp))
                }
            }

            // 2. COLLAPSING HERO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentHeroHeight)
                    .background(Brush.verticalGradient(headerGradientColors(isDark)))
            ) {
                // IMAGE ANIMATION: Center -> Left
                val imageSize = lerp(90.dp, 50.dp, scrollProgress)
                val imageX    = lerp((screenWidth / 2) - (imageSize / 2), 20.dp, scrollProgress)
                val imageY    = lerp(15.dp, 10.dp, scrollProgress)

                Box(
                    modifier = Modifier.offset { IntOffset(imageX.roundToPx(), imageY.roundToPx()) }
                ) {
                    with(sharedTransitionScope) {
                        AnimatedProfileImage(
                            imageUrl = user.imageUrl,
                            userName = user.name,
                            isActive = user.isActive,
                            size     = with(density) { imageSize.toPx().toInt() },
                            modifier = Modifier
                                .size(imageSize)
                                .sharedElement(
                                    sharedContentState      = rememberSharedContentState(key = "avatar_${user.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                        )
                    }
                }

                // NAME & STATUS ANIMATION
                val horizontalBias = 0f + (-0.65f - 0f) * scrollProgress
                val nameYOffset    = lerp(115.dp, 12.dp, scrollProgress)
                val nameScale      = 1.1f + (0.9f - 1.1f) * scrollProgress

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, nameYOffset.roundToPx()) }
                        .graphicsLayer {
                            scaleX = nameScale
                            scaleY = nameScale
                        },
                    horizontalAlignment = BiasAlignment.Horizontal(horizontalBias)
                ) {
                    val nudgeX = lerp(0.dp, 76.dp, scrollProgress)

                    Column(
                        modifier = Modifier.offset { IntOffset(nudgeX.roundToPx(), 0) },
                        horizontalAlignment = if (scrollProgress < 0.5f) Alignment.CenterHorizontally else Alignment.Start
                    ) {
                        Text(
                            text       = user.name,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White,
                            maxLines   = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusPill(isActive = user.isActive)
                    }
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
                softWrap   = false,
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
            modifier         = Modifier
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
