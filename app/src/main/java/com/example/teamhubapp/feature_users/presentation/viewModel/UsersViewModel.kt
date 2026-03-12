package com.example.teamhubapp.feature_users.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamhubapp.core.network.NetworkObserver
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import com.example.teamhubapp.feature_users.domain.usecase.FilterUsersUseCase
import com.example.teamhubapp.feature_users.domain.usecase.GetAvailableRolesUseCase
import com.example.teamhubapp.feature_users.domain.usecase.NormalizeUserNameUseCase
import com.example.teamhubapp.feature_users.domain.usecase.SortUsersUseCase
import com.example.teamhubapp.feature_users.presentation.state.UsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository,
    private val normalizeUserName: NormalizeUserNameUseCase,
    private val sortUsers: SortUsersUseCase,
    private val filterUsers: FilterUsersUseCase,
    private val getAvailableRoles: GetAvailableRolesUseCase,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    // ── Search ────────────────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // ── Role filter (null = All) ───────────────────────────────────────────────
    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole: StateFlow<String?> = _selectedRole.asStateFlow()

    fun onRoleSelected(role: String?) {
        _selectedRole.value = if (role == null || role == "All") null else role
    }

    // ── Activity filter (null = All, true = Active, false = Inactive) ─────────
    private val _selectedActivityFilter = MutableStateFlow<Boolean?>(null)
    val isActiveFilter: StateFlow<Boolean?> = _selectedActivityFilter.asStateFlow()

    fun onActivityFilterChange(isActive: Boolean?) {
        _selectedActivityFilter.value = isActive
    }

    // ── Network state ─────────────────────────────────────────────────────────
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _showOnlineBanner = MutableStateFlow(false)
    val showOnlineBanner: StateFlow<Boolean> = _showOnlineBanner.asStateFlow()

    private val _showOfflineBanner = MutableStateFlow(false)

    val showOfflineBanner: StateFlow<Boolean> = _showOfflineBanner.asStateFlow()

    // ── Pull-to-refresh ───────────────────────────────────────────────────────
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Add this with other flags
    private var _isForceRefreshing = MutableStateFlow(false)

    private var offlineBannerJob: kotlinx.coroutines.Job? = null
    private var onlineBannerJob: kotlinx.coroutines.Job? = null


    // ── Scroll position ───────────────────────────────────────────────────────
    private val _savedScrollIndex = MutableStateFlow(0)
    private val _savedScrollOffset = MutableStateFlow(0)
    fun saveScrollPosition(index: Int, offset: Int) {
        _savedScrollIndex.value = index
        _savedScrollOffset.value = offset
    }

    // ── UI State ──────────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    // ── Available roles (derived from live user list) ─────────────────────────
    val availableRoles = repository.observeUsers()
        .combine(_selectedRole) { users, _ -> getAvailableRoles(users) }


    private var hasLoadedOnce = false

    init {
        observeAndFilterUsers()
        observeNetwork()
        refresh()
    }

    // ── Observe + filter users ────────────────────────────────────────────────
    @OptIn(FlowPreview::class)
    private fun observeAndFilterUsers() {
        viewModelScope.launch {
            combine(
                repository.observeUsers(),
                _searchQuery.debounce(300),
                _selectedRole,
                _selectedActivityFilter
            ) { users, query, role, activityFilter ->
                // Stay in Loading if DB is empty on first launch —
                // don't jump to Empty before the first fetch completes
                if (users.isEmpty() && !hasLoadedOnce && _uiState.value is UsersUiState.Error) {
                    return@combine null
                }
                if (users.isEmpty() && _isForceRefreshing.value) {
                    return@combine null
                }

                applyFilters(users, query, role, activityFilter)
            }
                .collect { filteredUsers ->
                    filteredUsers ?: return@collect
                    if (!hasLoadedOnce) hasLoadedOnce = true
                    _uiState.value = if (filteredUsers.isEmpty()) {
                        UsersUiState.Empty
                    } else {
                        UsersUiState.Success(filteredUsers)
                    }
                }
        }
    }

    // ── Filter pipeline ───────────────────────────────────────────────────────
    private fun applyFilters(
        users: List<com.example.teamhubapp.feature_users.domain.model.User>,
        query: String,
        role: String?,
        activityFilter: Boolean?
    ) = filterUsers(
        users = sortUsers(normalizeUserName(users)),
        query = query,
        role = role,
        activityFilter = activityFilter
    )

    // ── Pull-to-refresh ───────────────────────────────────────────────────────
    fun forceRefresh() {
        viewModelScope.launch {
            val online = repository.isOnline()
            _isOnline.value = online
            if (!online) return@launch

            _isRefreshing.value = true
            _isForceRefreshing.value = true
            try {
                repository.forceRefresh()
            } catch (e: java.io.IOException) {
                _isOnline.value = false
            } catch (e: Exception) {
                _uiState.value = UsersUiState.Error("Something went wrong. Please try again.")
            } finally {
                _isRefreshing.value = false
                _isForceRefreshing.value = false
            }
        }
    }

    // ── Initial / reconnect refresh ───────────────────────────────────────────
    fun refresh() {
        viewModelScope.launch {
            _isOnline.value = repository.isOnline()
            if (!repository.isOnline()) {
                _uiState.value = UsersUiState.Error("No internet connection")
                return@launch
            }
            try {
                repository.refreshUsers()
            } catch (e: java.io.IOException) {
                _isOnline.value = false
                _uiState.value = UsersUiState.Error("No internet connection")
            } catch (e: Exception) {
                _uiState.value = UsersUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    // ── Network observer ──────────────────────────────────────────────────────
    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.isOnline.collect { online ->
                val wasOffline = !_isOnline.value
                _isOnline.value = online


                if (online && wasOffline) {

                    offlineBannerJob?.cancel()
                    onlineBannerJob?.cancel()

                    _showOfflineBanner.value = false
                    // Only show banner — don't auto-refresh
                    // User must tap "Try Again" to reload
                    onlineBannerJob = viewModelScope.launch {
                        _showOnlineBanner.value = true
                        kotlinx.coroutines.delay(3000)
                        _showOnlineBanner.value = false
                    }
                }


                // Auto-dismiss offline banner after 4 seconds
                if (!online) {
                    onlineBannerJob?.cancel()
                    offlineBannerJob?.cancel()

                    _showOnlineBanner.value = false

                    offlineBannerJob = viewModelScope.launch {
                        _showOfflineBanner.value = true
                        kotlinx.coroutines.delay(4000)
                        _showOfflineBanner.value = false
                    }
                }
            }
        }
    }
}