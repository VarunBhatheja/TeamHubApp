package com.example.teamhubapp.feature_users.presentation.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamhubapp.feature_users.data.remote.api.EmployeeApi
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import com.example.teamhubapp.feature_users.presentation.state.UsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


//For Hilt to provide dependencies inside ViewModels.

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    // ---------------------------
    // 🔎 Search State
    // ---------------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // ---------------------------
    // 🎭 Dynamic Role Filter
    // null = All roles
    // ---------------------------
    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole = _selectedRole.asStateFlow()

    fun onRoleSelected(role: String?) {
        _selectedRole.value = role
    }

    // ---------------------------
    // 🟢 Active Only Filter
    // ---------------------------
    private val _activeOnly = MutableStateFlow(false)
    val activeOnly = _activeOnly.asStateFlow()

    fun onActiveOnlyChanged(enabled: Boolean) {
        _activeOnly.value = enabled
    }

    // ---------------------------
    // 🧠 UI State
    // ---------------------------
    private val _uiState =
        MutableStateFlow<UsersUiState>(UsersUiState.Loading)

    val uiState: StateFlow<UsersUiState> = _uiState

// ---------------------------
// 📋 Available Roles (Dynamic)

    val availableRoles = repository.observeUsers()
        .combine(_selectedRole) { users, _ ->
            users.map { it.designation }
                .distinct()
                .sorted()
        }

    init {
        observeUsers()
        refresh()
    }

    private fun observeUsers() {
        viewModelScope.launch {
            repository.observeUsers()
                .combine(_searchQuery) { users, query ->
                    users to query
                }
                .combine(_selectedRole) { (users, query), role ->
                    Triple(users, query, role)
                }
                .combine(_activeOnly) { (users, query, role), activeOnly ->

                    var filtered = users

                    // 🔎 Search by name
                    if (query.isNotBlank()) {
                        filtered = filtered.filter {
                            it.name.contains(query, ignoreCase = true)
                        }
                    }

                    // 🎭 Role filter (Dynamic)
                    role?.let {
                        filtered = filtered.filter { user ->
                            user.designation == it
                        }
                    }

                    // 🟢 Active only filter
                    if (activeOnly) {
                        filtered = filtered.filter { it.isActive }
                    }

                    filtered
                }
                .collect { filteredUsers ->

                    _uiState.value =
                        if (filteredUsers.isEmpty()) {
                            UsersUiState.Empty
                        } else {
                            UsersUiState.Success(filteredUsers)
                        }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.refreshUsers()
            } catch (e: Exception) {
                _uiState.value =
                    UsersUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}