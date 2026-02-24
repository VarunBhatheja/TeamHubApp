package com.example.teamhubapp.feature_users.data.repository

import com.example.teamhubapp.feature_users.data.local.UserDao
import com.example.teamhubapp.feature_users.data.mapper.toDomain
import com.example.teamhubapp.feature_users.data.mapper.toEntity
import com.example.teamhubapp.feature_users.data.remote.api.EmployeeApi
import com.example.teamhubapp.feature_users.domain.model.User
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl @Inject constructor(
    private val api: EmployeeApi,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun refreshUsers() {


            // Call API
            val response = api.getEmployees()

            // Extract DTO list
            val employeeDtos = response.data?.employees ?: emptyList()

            // Convert DTO → Domain
            val users = employeeDtos.map { it.toDomain() }

            // Convert Domain → Entity
            val entities = users.map { it.toEntity() }

            // Clear old data
            userDao.clearUsers()

            // Insert new data
            userDao.insertUsers(entities)


    }

    override suspend fun getUserById(id: String): User? {
        TODO("Not yet implemented")
    }

    override fun observeUsers(): Flow<List<User>> {
        return userDao.observeUsers()
            .map{ entities ->
                entities.map { it.toDomain() }
            }
    }

}
