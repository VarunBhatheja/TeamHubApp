package com.example.teamhubapp.feature_users.domain.usecase

import com.example.teamhubapp.feature_users.domain.model.User
import jakarta.inject.Inject

class GetAvailableDepartmentsUseCase @Inject constructor() {
    operator fun invoke(users: List<User>): List<String> {
        return users
            .map { it.department }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
}