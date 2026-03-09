package com.example.teamhubapp.feature_users.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import com.example.teamhubapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository : UserRepository,
    savedStateHandle       : SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(
        savedStateHandle[Screen.UserDetail.ARG_USER_ID]
    )

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    init {
        loadUser(userId)
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            repository.getUserById(userId)
                .catch { e ->
                    _uiState.value = UserDetailUiState.Error(
                        e.message ?: "Something went wrong"
                    )
                }
                .collect { user ->
                    _uiState.value = if (user != null) {
                        UserDetailUiState.Success(user)
                    } else {
                        UserDetailUiState.Error("User not found")
                    }
                }
        }
    }
}