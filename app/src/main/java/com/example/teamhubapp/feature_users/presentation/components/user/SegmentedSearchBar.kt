package com.example.teamhubapp.feature_users.presentation.components.user

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Drives the isActive filter in ViewModel
// null = ALL, true = ACTIVE, false = INACTIVE
enum class ActivityFilter { ALL, ACTIVE, INACTIVE }


// Search input with leading icon — underline hidden, clean card look


@Composable
fun SearchInputField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        placeholder = {
            Text(
                text = "Search employees...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,   // hide underline
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}






// Segmented pill group: [ All | Active | Inactive ]
// Selected pill fills with primary blue, others stay surface color
// animateColorAsState gives smooth transition between states
@Composable
fun ActivityFilterPills(
    selected: ActivityFilter,
    onSelect: (ActivityFilter) -> Unit
) {
    val options = listOf(
        ActivityFilter.ALL to "All",
        ActivityFilter.ACTIVE to "Active",
        ActivityFilter.INACTIVE to "Inactive"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        options.forEachIndexed { index, (filter, label) ->
            val isSelected = selected == filter

            // Smooth color transitions on selection change
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                animationSpec = tween(200),
                label = "bg_$label"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "text_$label"
            )

            Box(
                modifier = Modifier
                    .background(bgColor)
                    // First pill: rounded left only, last: rounded right only
                    .clip(
                        when (index) {
                            0 -> RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp)
                            options.size - 1 -> RoundedCornerShape(topEnd = 7.dp, bottomEnd = 7.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor
                )
            }

            // Separator line between pills — not after last
            if (index < options.size - 1) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        }
    }
}

// Role dropdown — dynamic list from API
// "All" prepended if not already present
// Selected item shows filled blue circle checkmark
@Composable
fun StyledRoleDropdown(
    expanded: Boolean,
    roles: List<String>,
    selectedRole: String?,
    onRoleSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val allRoles = if (roles.contains("All")) roles else listOf("All") + roles

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(200.dp)
            .heightIn(max = 260.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        offset = DpOffset(x = 0.dp, y = 0.dp)
    ) {
        Text(
            text = "FILTER BY ROLE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        allRoles.forEach { role ->
            val isSelected = when {
                role == "All" -> selectedRole == null || selectedRole == "All"
                else -> role == selectedRole
            }

            // Highlight selected row with subtle blue tint
            val itemBg by animateColorAsState(
                targetValue = if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                else Color.Transparent,
                animationSpec = tween(150),
                label = "itemBg_$role"
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = role,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = { onRoleSelected(role) },
                leadingIcon = {
                    if (isSelected) {
                        // Filled circle with checkmark — selected state
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check, null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    } else {
                        // Empty circle — unselected state
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(itemBg)
                    .padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}