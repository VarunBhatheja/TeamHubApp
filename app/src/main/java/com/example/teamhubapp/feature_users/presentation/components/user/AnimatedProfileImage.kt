package com.example.teamhubapp.feature_users.presentation.components.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

// Circular avatar with:
// - Scale-in entrance animation
// - Sweep gradient status ring (blue = active, muted = inactive)
// - Blue glow shadow for active users
// - Shimmer placeholder while image loads
// - Initials fallback if image fails
// size param allows reuse at different sizes (list = 54, detail = 110)
@Composable
fun AnimatedProfileImage(
    imageUrl: String,
    userName: String,
    isActive: Boolean = true,
    size: Int = 54
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Scale from 0.8 → 1.0 on first composition — subtle pop-in
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(450),
        label = "profileScale"
    )

    val ringActive = MaterialTheme.colorScheme.primary
    val ringInactive = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .padding(
                start = if (size == 54) 14.dp else 0.dp,
                top = if (size == 54) 12.dp else 0.dp,
                bottom = if (size == 54) 12.dp else 0.dp
            )
            .size(size.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Active: blue glow elevates avatar off the card
                // Inactive: near-invisible shadow — recessed feel
                .shadow(
                    elevation = if (isActive) 8.dp else 1.dp,
                    shape = CircleShape,
                    ambientColor = ringActive.copy(alpha = 0.3f),
                    spotColor = if (isActive) ringActive.copy(alpha = 0.4f)
                    else Color.Transparent
                )
                // Sweep gradient ring — gives shiny arc effect
                .border(
                    width = 2.dp,
                    brush = if (isActive)
                        Brush.sweepGradient(
                            listOf(
                                ringActive,
                                ringActive.copy(alpha = 0.4f),
                                ringActive
                            )
                        )
                    else
                        Brush.sweepGradient(
                            listOf(
                                ringInactive,
                                ringInactive.copy(alpha = 0.3f),
                                ringInactive
                            )
                        ),
                    shape = CircleShape
                )
                .padding(3.dp)   // gap between ring and image edge
                .clip(CircleShape)
        ) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = userName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Shimmer-style gradient while image loads
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )
                                )
                            )
                    )
                },
                // Initials avatar if image URL fails
                // Color is deterministic — same user always gets same color
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(avatarGradientForName(userName))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialsFromName(userName),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}

// "Ankur Chauhan" → "AC" | "sumit" → "S" | "" → "?"
private fun initialsFromName(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.isNotEmpty() && parts[0].isNotEmpty() -> parts[0].first().uppercase()
        else -> "?"
    }
}

// hashCode % palette size = same user always gets same color
private val avatarPalette = listOf(
    listOf(Color(0xFF2D7DD2), Color(0xFF1A5FA8)),  // blue
    listOf(Color(0xFF43AA8B), Color(0xFF2D8B6F)),  // teal
    listOf(Color(0xFFF4845F), Color(0xFFD4633F)),  // coral
    listOf(Color(0xFF7B5EA7), Color(0xFF5C3D8A)),  // purple
    listOf(Color(0xFFE9A84A), Color(0xFFC98A2A)),  // gold
    listOf(Color(0xFF4ECDC4), Color(0xFF2BADA5)),  // cyan
    listOf(Color(0xFFEF6C6C), Color(0xFFCC4E4E)),  // red
)

private fun avatarGradientForName(name: String): List<Color> {
    val index = kotlin.math.abs(name.hashCode()) % avatarPalette.size
    return avatarPalette[index]
}