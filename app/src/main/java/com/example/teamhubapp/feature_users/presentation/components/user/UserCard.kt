package com.example.teamhubapp.feature_users.presentation.components.user

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamhubapp.feature_users.domain.model.User

@Composable
fun UserCard(
    user     : User,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier
) {
    val cardColor = if (user.isActive)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier  = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(350)),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (user.isActive) 5.dp else 2.dp,
            pressedElevation = 10.dp
        )
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent strip — bold blue for active, muted for inactive
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = if (user.isActive)
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            else
                                listOf(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                        )
                    )
            )

            AnimatedProfileImage(
                imageUrl = user.imageUrl,
                userName = user.name,
                isActive = user.isActive
            )

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 46.dp, top = 14.dp, bottom = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                            color      = if (user.isActive)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(isActive = user.isActive)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text       = user.designation,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (user.isActive)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }

                // Navigate to detail arrow
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "View Details",
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}