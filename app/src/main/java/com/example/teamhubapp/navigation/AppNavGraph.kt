package com.example.teamhubapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.teamhubapp.feature_users.presentation.detail.UserDetailScreen
import com.example.teamhubapp.feature_users.presentation.users.UsersScreen

/**
 * Single source of truth for all navigation in the app.
 *
 * Screens are completely decoupled from each other —
 * they communicate only through lambdas, never through
 * NavController directly. This makes each screen
 * independently testable and previewable.
 *
 * Screens defined here:
 *  1. [Screen.UsersList]  → Employee list
 *  2. [Screen.UserDetail] → Employee detail
 */

@Composable
fun AppNavGraph(
    navController : NavHostController = rememberNavController(),
    onToggleTheme : () -> Unit,
    isDarkMode    : Boolean
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.UsersList.route
    ) {

        // Employee list screen
        composable(route = Screen.UsersList.route) {
            UsersScreen(
                onUserClick = { userId ->
                    // Guard against double navigation on rapid taps
                    if (navController.currentDestination?.route
                        == Screen.UsersList.route
                    ) {
                        navController.navigate(
                            Screen.UserDetail.createRoute(userId)
                        )
                    }
                },
                onToggleTheme = onToggleTheme,
                isDarkMode    = isDarkMode
            )
        }

        // Employee detail screen
        composable(
            route     = Screen.UserDetail.route,
            arguments = listOf(
                navArgument(Screen.UserDetail.ARG_USER_ID) {
                    type     = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString(Screen.UserDetail.ARG_USER_ID)
                ?: return@composable

            UserDetailScreen(
                userId      = userId,
                onBackClick = {
                    // Guard against empty back stack
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}