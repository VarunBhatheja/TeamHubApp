package com.example.teamhubapp.feature_users.domain.usecase

import com.example.teamhubapp.feature_users.domain.model.User
import javax.inject.Inject

// Sorts users:
// Rule 1: Real names come before "Employee X" names
// Rule 2: Real names sorted alphabetically
// Rule 3: Employee X sorted numerically (1, 2, 9, 10 — not 1, 10, 2, 9)
class SortUsersUseCase @Inject constructor() {

    operator fun invoke(users: List<User>): List<User> {
        return users.sortedWith(
            Comparator { a, b ->
                val aIsGeneric = isGenericName(a.name)
                val bIsGeneric = isGenericName(b.name)
                when {
                    // Both real names → alphabetical
                    !aIsGeneric && !bIsGeneric ->
                        a.name.lowercase().compareTo(b.name.lowercase())
                    // Both Employee X → numeric
                    aIsGeneric && bIsGeneric ->
                        extractNumber(a.name).compareTo(extractNumber(b.name))
                    // Real name always before Employee X
                    !aIsGeneric && bIsGeneric -> -1
                    else -> 1
                }
            }
        )
    }

    private fun isGenericName(name: String): Boolean {
        return name.trim().matches(Regex("(?i)employee[\\s.#no]*\\d+.*"))
    }

    private fun extractNumber(name: String): Int {
        return Regex("\\d+").find(name)?.value?.toIntOrNull() ?: Int.MAX_VALUE
    }
}