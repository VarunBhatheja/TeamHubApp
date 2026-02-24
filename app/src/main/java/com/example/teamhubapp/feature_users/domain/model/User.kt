package com.example.teamhubapp.feature_users.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val designation: String,
    val department: String,
    val isActive: Boolean,
    val imageUrl: String,
    val city: String,
    val country: String,
    val joiningDate: String,
)