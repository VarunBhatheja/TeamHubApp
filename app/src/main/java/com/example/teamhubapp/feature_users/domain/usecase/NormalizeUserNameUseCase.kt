package com.example.teamhubapp.feature_users.domain.usecase


import com.example.teamhubapp.feature_users.domain.model.User
import javax.inject.Inject

// Capitalizes first letter of each word in user's name

class NormalizeUserNameUseCase @Inject constructor() {

    operator fun invoke(users: List<User>): List<User> {
        return users.map { user ->
            user.copy(
                name = user.name
                    .split(" ")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar { it.uppercase() }
                    }
            )
        }
    }
}