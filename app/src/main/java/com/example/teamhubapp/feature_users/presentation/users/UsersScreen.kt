package com.example.teamhubapp.feature_users.presentation.users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersScreen(
    onUserClick   : (String) -> Unit,
    onToggleTheme : () -> Unit,
    isDarkMode    : Boolean,
    viewModel     : UsersViewModel = hiltViewModel()
) {
    val uiState           by viewModel.uiState.collectAsState()
    val selectedRole      by viewModel.selectedRole.collectAsState()
    val availableRoles    by viewModel.availableRoles.collectAsState(initial = emptyList())
    val searchQuery       by viewModel.searchQuery.collectAsState()
    val isRefreshing      by viewModel.isRefreshing.collectAsState()
    val isActiveFilter    by viewModel.isActiveFilter.collectAsState()
    val showOnlineBanner  by viewModel.showOnlineBanner.collectAsState()
    val showOfflineBanner by viewModel.showOfflineBanner.collectAsState()

    val activityFilter = when (isActiveFilter) {
        true  -> ActivityFilter.ACTIVE
        false -> ActivityFilter.INACTIVE
        null  -> ActivityFilter.ALL
    }

    // Track previous filter values to detect real changes vs recomposition
    var prevRole   by remember { mutableStateOf(selectedRole) }
    var prevFilter by remember { mutableStateOf(activityFilter) }
    var prevQuery  by remember { mutableStateOf(searchQuery) }

    val isDark    = isSystemInDarkTheme()
    val listState = rememberLazyListState()

    // Scroll to top only when a filter actually changes
    LaunchedEffect(selectedRole, activityFilter, searchQuery) {
        val filterChanged = selectedRole   != prevRole   ||
                activityFilter != prevFilter ||
                searchQuery    != prevQuery
        if (filterChanged) {
            listState.animateScrollToItem(0)
            viewModel.saveScrollPosition(0, 0)
        }
        prevRole   = selectedRole
        prevFilter = activityFilter
        prevQuery  = searchQuery
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
        // Background gradient bleeds under status bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = headerGradientColors(isDark),
                        endY = 420f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            HeaderSection(
                isDarkMode    = isDarkMode,
                onToggleTheme = onToggleTheme
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),  // ← one padding, both inherit
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                SearchInputField(
                    text = searchQuery,
                    onTextChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Activity + Role filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActivityFilterPills(
                        selected = activityFilter,
                        onSelect = { filter ->
                            viewModel.onActivityFilterChange(
                                when (filter) {
                                    ActivityFilter.ALL -> null
                                    ActivityFilter.ACTIVE -> true
                                    ActivityFilter.INACTIVE -> false
                                }
                            )
                        }
                    )

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        var showRoleDropdown by remember { mutableStateOf(false) }


                        // Tappable role pill with animated arrow
                        val arrowRotation by animateFloatAsState(
                            targetValue = if (showRoleDropdown) 180f else 0f,
                            animationSpec = tween(200),
                            label = "arrowRotation"
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .clickable { showRoleDropdown = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = selectedRole ?: "Role",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select role",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer { rotationZ = arrowRotation }
                            )
                        }


                        StyledRoleDropdown(
                            expanded = showRoleDropdown,
                            roles = availableRoles,
                            selectedRole = selectedRole,
                            onRoleSelected = {
                                viewModel.onRoleSelected(it)
                                showRoleDropdown = false
                            },
                            onDismiss = { showRoleDropdown = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Card tray — rounded top corners
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
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
                                text       = "You're offline Now ",
                                fontSize   = 13.sp,
                                color      = Color(0xFF856404),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Back-online banner (auto-dismisses after 3s)
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
                            // "During pull-to-refresh, ShimmerLoadingView is shown
                            //instead of EmptyView to prevent confusing empty state
                            //flash while data reloads from API."
                            is UsersUiState.Empty   -> ShimmerLoadingView()
                            is UsersUiState.Error -> ErrorView(
                                message = state.message,
                                onRetry = { viewModel.refresh() }   // retry hits API again
                            )
                            is UsersUiState.Success -> UsersList(
                                users       = state.users,
                                onUserClick = onUserClick,
                                listState   = listState,
                                modifier    = Modifier.padding(top = 24.dp)
                            )
                        }
                    }
                }

                // Pull-to-refresh spinner — must be LAST to float on top
                PullRefreshIndicator(
                    refreshing   = isRefreshing,
                    state        = pullRefreshState,
                    modifier     = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
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

        // Brand accent underline
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