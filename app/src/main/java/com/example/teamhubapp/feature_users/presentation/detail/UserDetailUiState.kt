package com.example.teamhubapp.feature_users.presentation.detail

import com.example.teamhubapp.feature_users.domain.model.User

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: User) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}
