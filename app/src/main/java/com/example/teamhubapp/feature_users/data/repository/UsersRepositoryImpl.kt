package com.example.teamhubapp.feature_users.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.teamhubapp.feature_users.data.local.UserDao
import com.example.teamhubapp.feature_users.data.mapper.toDomain
import com.example.teamhubapp.feature_users.data.mapper.toEntity
import com.example.teamhubapp.feature_users.data.remote.api.EmployeeApi
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api     : EmployeeApi,
    private val userDao : UserDao,
    @ApplicationContext private val context: Context
) : UserRepository {

    // Fetch from API → save to Room
    // Called on app start
    override suspend fun refreshUsers() {
        if (!isOnline()) return

        val response = try {
            api.getEmployees()          // attempt — wakes Render if sleeping
        } catch (e: Exception) {
            api.getEmployees()          // retry after wake
        }

        val entities = (response.data?.employees ?: emptyList())
            .map { it.toDomain().toEntity() }

        userDao.clearUsers()
        userDao.insertUsers(entities)
    }

    // Force fresh data — skips online check
    // Called on pull-to-refresh
    override suspend fun forceRefresh() {
        val response = api.getEmployees()
        val entities = (response.data?.employees ?: emptyList())
            .map { it.toDomain().toEntity() }

        userDao.clearUsers()
        userDao.insertUsers(entities)
    }

    // Continuous stream of users from Room
    override fun observeUsers(): Flow<List<User>> {
        return userDao.observeUsers()
            .map { entities -> entities.map { it.toDomain() } }
    }

    // Single user stream for detail screen
    override fun getUserById(id: String): Flow<User?> {
        return userDao.getUserById(id)
            .map { it?.toDomain() }
    }

    // Device connectivity check
    override fun isOnline(): Boolean {
        val cm   = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val caps = cm.activeNetwork
            ?.let { cm.getNetworkCapabilities(it) }
            ?: return false
        return caps.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}
