package com.example.teamhubapp.feature_users.presentation.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

// Shows 6 skeleton cards while data loads
@Composable
fun ShimmerLoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(6) { ShimmerUserCard() }
    }
}

// Mirrors UserCard layout — avatar + two text lines + badge placeholder
@Composable
fun ShimmerUserCard() {
    val shimmerBrush = rememberShimmerBrush()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(shimmerBrush)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Name line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
            // Designation line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(11.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
        }

        // Status badge placeholder
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(shimmerBrush)
        )
    }
}

// Animated left-to-right sweep brush — shared across all skeleton shapes
@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start  = Offset.Zero,
        end    = Offset(x = translateAnim, y = translateAnim)
    )
}