package com.example.teamhubapp.feature_users.domain.usecase

import com.example.teamhubapp.feature_users.domain.model.User
import javax.inject.Inject

// Extracts unique sorted roles from user list for the Role filter dropdown
class GetAvailableRolesUseCase @Inject constructor() {

    operator fun invoke(users: List<User>): List<String> {
        return users
            .map { it.designation
                .split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
            }
            .distinct()
            .sorted()
    }
}