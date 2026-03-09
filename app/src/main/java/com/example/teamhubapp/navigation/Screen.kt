package com.example.teamhubapp.navigation

import com.example.teamhubapp.navigation.Screen.UserDetail.ARG_USER_ID


// Sealed class ensures type-safe navigation routes
sealed class Screen(val route: String) {

    // No arguments
    object UsersList : Screen("users_list")

    // Requires userId as path argument
    object UserDetail : Screen("user_detail/{userId}") {
        const val ARG_USER_ID = "userId"
        // "user_detail/abc123"
        fun createRoute(userId: String) = "user_detail/$userId"
    }
}