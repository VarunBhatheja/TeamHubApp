package com.example.teamhubapp.feature_users.presentation.components.user

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

// Pulsing dot + label — active dot pulses, inactive dot is static
@Composable
fun StatusBadge(isActive: Boolean?) {
    val color = if (isActive == true) Color(0xFF2E7D32) else Color(0xFFC62828)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.4f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseValue"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .graphicsLayer {
                    // Only active dot pulses — inactive stays still
                    if (isActive == true) { scaleX = pulse; scaleY = pulse }
                }
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text  = if (isActive == true) "Active" else "Inactive",
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}