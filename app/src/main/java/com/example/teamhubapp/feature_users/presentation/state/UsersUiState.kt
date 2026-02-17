package com.example.teamhubapp.feature_users.presentation.state

import com.example.teamhubapp.feature_users.domain.model.User

// A sealed class allows you to keep the hierarchy "internal" to your library while keeping
// the API public, ensuring users cannot alter internal logic by creating unauthorized subclasses.
sealed interface UsersUiState {

    object Loading : UsersUiState

    data class Success(
        val users: List<User>
    ) : UsersUiState

    data class Error(
        val message: String
    ) : UsersUiState

    object Empty : UsersUiState
}