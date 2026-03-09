package com.example.teamhubapp.feature_users.domain.usecase

import com.example.teamhubapp.feature_users.domain.model.User
import javax.inject.Inject

// Handles ALL filtering in one place:
// 1. Search by name
// 2. Filter by role/designation
// 3. Filter by active/inactive status


class FilterUsersUseCase @Inject constructor() {

    operator fun invoke(
        users: List<User>,
        query: String,
        role: String?,
        activityFilter: Boolean?
    ): List<User> {
        var filtered = users

        // Search filter
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        // Role filter — null means show all roles
        if (role != null) {
            filtered = filtered.filter {
                it.designation == role
            }
        }

        // Activity filter
        // null  = show all
        // true  = active only
        // false = inactive only
        when (activityFilter) {
            true  -> filtered = filtered.filter { it.isActive  }
            false -> filtered = filtered.filter { !!it.isActive }
            null  -> { }
        }

        return filtered
    }
}