package com.example.teamhubapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pill button in the header — sun icon = currently dark, moon icon = currently light
@Composable
fun ThemeToggleButton(
    isDarkMode : Boolean,
    onToggle   : () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onToggle
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                // Sun shown in dark mode = "tap to go light"
                // Moon shown in light mode = "tap to go dark"
                imageVector        = if (isDarkMode) Icons.Outlined.WbSunny
                else Icons.Outlined.NightlightRound,
                contentDescription = if (isDarkMode) "Switch to light mode"
                else "Switch to dark mode",
                tint               = Color.White,
                modifier           = Modifier.size(16.dp)
            )
            Text(
                text       = if (isDarkMode) "Light" else "Dark",
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                color      = Color.White
            )
        }
    }
}