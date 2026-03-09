package com.example.teamhubapp.feature_users.domain.repository

import com.example.teamhubapp.feature_users.domain.model.User
import kotlinx.coroutines.flow.Flow

// Contract between domain and data layer
// Domain defines WHAT, data layer defines HOW
interface UserRepository {

    // Continuous stream of users — auto-updates when DB changes
    fun observeUsers(): Flow<List<User>>

    // One-shot fetch from API → saves to Room
    suspend fun refreshUsers()

    // Pull-to-refresh — clears old data then fetches fresh
    suspend fun forceRefresh()

    // Single user stream for detail screen
    fun getUserById(id: String): Flow<User?>

    // Network connectivity check
    fun isOnline(): Boolean
}
