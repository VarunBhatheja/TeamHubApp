package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teamhubapp.feature_users.presentation.components.common.EmptyView
import com.example.teamhubapp.feature_users.presentation.components.common.ErrorView
import com.example.teamhubapp.feature_users.presentation.components.common.ShimmerLoadingView
import com.example.teamhubapp.feature_users.presentation.components.user.ActivityFilter
import com.example.teamhubapp.feature_users.presentation.components.user.ActivityFilterPills
import com.example.teamhubapp.feature_users.presentation.components.user.SearchInputField
import com.example.teamhubapp.feature_users.presentation.components.user.StyledRoleDropdown
import com.example.teamhubapp.feature_users.presentation.state.UsersUiState
import com.example.teamhubapp.feature_users.presentation.viewModel.UsersViewModel
import com.example.teamhubapp.ui.theme.ThemeToggleButton
import com.example.teamhubapp.ui.theme.headerGradientColors

@OptIn(ExperimentalMaterialApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun UsersScreen(
    onUserClick             : (String) -> Unit,
    onToggleTheme           : () -> Unit,
    isDarkMode              : Boolean,
    sharedTransitionScope   : SharedTransitionScope,
    animatedVisibilityScope : AnimatedVisibilityScope,
    viewModel               : UsersViewModel = hiltViewModel()
) {
    val uiState              by viewModel.uiState.collectAsState()
    val selectedRole         by viewModel.selectedRole.collectAsState()
    val availableRoles       by viewModel.availableRoles.collectAsState(initial = emptyList())
    val searchQuery          by viewModel.searchQuery.collectAsState()
    val isRefreshing         by viewModel.isRefreshing.collectAsState()
    val isActiveFilter       by viewModel.isActiveFilter.collectAsState()
    val showOnlineBanner     by viewModel.showOnlineBanner.collectAsState()
    val showOfflineBanner    by viewModel.showOfflineBanner.collectAsState()
    val selectedDepartment   by viewModel.selectedDepartment.collectAsState()
    val availableDepartments by viewModel.availableDepartments.collectAsState(initial = emptyList())
    var lastScrollOffset by remember { mutableStateOf(0) }
    var lastScrollIndex  by remember { mutableStateOf(0) }
    var filtersVisible   by remember { mutableStateOf(true) }


    val listState = rememberLazyListState()


    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to
                    listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->

            val isScrollingUp = when {
                index  < lastScrollIndex  -> true
                index == lastScrollIndex  -> offset < lastScrollOffset
                else                      -> false
            }

            filtersVisible   = isScrollingUp
            lastScrollIndex  = index
            lastScrollOffset = offset
        }
    }

    val activityFilter = when (isActiveFilter) {
        true  -> ActivityFilter.ACTIVE
        false -> ActivityFilter.INACTIVE
        null  -> ActivityFilter.ALL
    }

    var prevQuery by remember { mutableStateOf(searchQuery) }
    val isDark    = isSystemInDarkTheme()


    LaunchedEffect(searchQuery) {
        if (searchQuery != prevQuery) {
            listState.animateScrollToItem(0)
            viewModel.saveScrollPosition(0, 0)
        }
        prevQuery = searchQuery
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh  = { viewModel.forceRefresh() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = headerGradientColors(isDark),
                        endY   = 420f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // ── Header ────────────────────────────────────────────────────
            HeaderSection(
                isDarkMode    = isDarkMode,
                onToggleTheme = onToggleTheme
            )

            // ── Search + collapsing filters ───────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(

                            stiffness    = Spring.StiffnessMediumLow
                        )
                    )
            ) {
                // Search — always visible
                SearchInputField(
                    text         = searchQuery,
                    onTextChange = { viewModel.onSearchQueryChange(it) },
                    modifier     = Modifier.padding(horizontal = 16.dp)
                )

                // Filters — collapse when scrolled down
                AnimatedVisibility(
                    visible = filtersVisible,
                    enter   = expandVertically(
                        expandFrom    = Alignment.Top,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing         = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            easing         = FastOutSlowInEasing
                        )
                    ),
                    exit    = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(
                            durationMillis = 250,
                            easing         = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = 150,
                            easing         = FastOutSlowInEasing
                        )
                    )
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        // ── Activity + Role row ───────────────────────────
                        Row(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ActivityFilterPills(
                                selected = activityFilter,
                                onSelect = { filter ->
                                    viewModel.onActivityFilterChange(
                                        when (filter) {
                                            ActivityFilter.ALL      -> null
                                            ActivityFilter.ACTIVE   -> true
                                            ActivityFilter.INACTIVE -> false
                                        }
                                    )
                                }
                            )

                            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                var showRoleDropdown by remember { mutableStateOf(false) }

                                val arrowRotation by animateFloatAsState(
                                    targetValue   = if (showRoleDropdown) 180f else 0f,
                                    animationSpec = tween(200),
                                    label         = "arrowRotation"
                                )

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                        .clickable { showRoleDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text       = selectedRole ?: "Role",
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector        = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select role",
                                        tint               = MaterialTheme.colorScheme.primary,
                                        modifier           = Modifier
                                            .size(16.dp)
                                            .graphicsLayer { rotationZ = arrowRotation }
                                    )
                                }

                                StyledRoleDropdown(
                                    expanded       = showRoleDropdown,
                                    roles          = availableRoles,
                                    selectedRole   = selectedRole,
                                    onRoleSelected = {
                                        viewModel.onRoleSelected(it)
                                        showRoleDropdown = false
                                    },
                                    onDismiss = { showRoleDropdown = false }
                                )
                            }
                        }

                        // ── Department pills ──────────────────────────────
                        if (availableDepartments.isNotEmpty()) {
                            LazyRow(
                                contentPadding        = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                item {
                                    DepartmentFilterPills(
                                        departments = availableDepartments,
                                        selected    = selectedDepartment,
                                        onSelect    = { viewModel.onDepartmentSelected(it) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                } // ← AnimatedVisibility ends HERE ✅
            }     // ← filters Column ends HERE ✅

            // ── Card tray — always visible, never collapses ───────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )

                Column(modifier = Modifier.fillMaxSize()) {

                    // Offline banner
                    AnimatedVisibility(
                        visible = showOfflineBanner,
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF3CD))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Outlined.WifiOff,
                                contentDescription = null,
                                tint               = Color(0xFF856404),
                                modifier           = Modifier.size(16.dp)
                            )
                            Text(
                                text       = "You're offline",
                                fontSize   = 13.sp,
                                color      = Color(0xFF856404),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Online banner
                    AnimatedVisibility(
                        visible = showOnlineBanner,
                        enter   = fadeIn() + slideInVertically(),
                        exit    = fadeOut() + slideOutVertically()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD4EDDA))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Outlined.Wifi,
                                contentDescription = null,
                                tint               = Color(0xFF155724),
                                modifier           = Modifier.size(16.dp)
                            )
                            Text(
                                text       = "You're back online",
                                fontSize   = 13.sp,
                                color      = Color(0xFF155724),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Main content
                    when {
                        isRefreshing && uiState !is UsersUiState.Success -> ShimmerLoadingView()
                        else -> when (val state = uiState) {
                            is UsersUiState.Loading -> ShimmerLoadingView()
                            is UsersUiState.Empty   -> EmptyView()
                            is UsersUiState.Error   -> ErrorView(
                                message  = state.message,
                                onRetry  = { viewModel.refresh() },
                                modifier = Modifier.padding(top = 48.dp)
                            )
                            is UsersUiState.Success -> UsersList(
                                users                   = state.users,
                                onUserClick             = onUserClick,
                                listState               = listState,
                                modifier                = Modifier.padding(top = 24.dp),
                                sharedTransitionScope   = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }

                // Pull-to-refresh spinner — always on top
                PullRefreshIndicator(
                    refreshing   = isRefreshing,
                    state        = pullRefreshState,
                    modifier     = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            } // ← Card tray Box ends
        }     // ← outer Column ends
    }         // ← root Box ends
}

@Composable
fun DepartmentFilterPills(
    departments : List<String>,
    selected    : String?,
    onSelect    : (String?) -> Unit
) {
    val options = listOf(null) + departments

    Column {                                    // ← wrap in Column for divider

        HorizontalDivider(                     // ← divider ABOVE
            color     = MaterialTheme.colorScheme.outlineVariant,

        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            options.forEachIndexed { index, dept ->
                val isSelected = selected == dept
                val label      = dept ?: "All"

                val bgColor by animateColorAsState(
                    targetValue   = if (isSelected) MaterialTheme.colorScheme.primaryContainer  // ← changed
                    else MaterialTheme.colorScheme.surface,
                    animationSpec = tween(200),
                    label         = "bg_$label"
                )
                val textColor by animateColorAsState(
                    targetValue   = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer // ← changed
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(200),
                    label         = "text_$label"
                )

                Box(
                    modifier = Modifier
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { onSelect(dept) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = label,
                        fontSize   = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = textColor
                    )
                }

                if (index < options.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                                                        // ← explicit width
                            .background(MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }

        HorizontalDivider(                     // ← divider BELOW
            color     = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}
@Composable
private fun HeaderSection(
    isDarkMode    : Boolean,
    onToggleTheme : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
            .padding(top = 16.dp, bottom = 4.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text          = "Employees",
                modifier      = Modifier.weight(1f),
                fontSize      = 34.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-0.8).sp
            )
            ThemeToggleButton(
                isDarkMode = isDarkMode,
                onToggle   = onToggleTheme
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(48.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3D8EF0), Color(0xFF85BFFF))
                    )
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text       = "Your team at a glance",
            fontSize   = 13.sp,
            color      = Color.White.copy(alpha = 0.55f),
            fontWeight = FontWeight.Normal
        )
    }
}