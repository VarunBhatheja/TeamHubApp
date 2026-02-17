package com.example.teamhubapp.feature_users.domain.repository



import com.example.teamhubapp.feature_users.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    /**
     * Observe list of users.
     * Used by Users List screen.
     * Must emit cached data and update when refreshed.
     */
    fun observeUsers(): Flow<List<User>>

    /**
     * Force refresh from remote API.
     * Used for pull-to-refresh.
     */
    suspend fun refreshUsers()

    /**
     * Get single user for detail screen.
     */
    suspend fun getUserById(id: String): User?
}
