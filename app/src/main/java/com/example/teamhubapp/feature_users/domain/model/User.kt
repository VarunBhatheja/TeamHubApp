package com.example.teamhubapp.feature_users.domain.model

// Core domain model — single source of truth for user data
// Pure Kotlin — no Android, Room, or Retrofit dependencies
data class User(
    val id          : String  = "",
    val name        : String  = "",
    val email       : String  = "",
    val designation : String  = "",
    val department  : String  = "",
    val isActive    : Boolean = false  ,
    val imageUrl    : String  = "",
    val city        : String  = "",
    val country     : String  = "",
    val joiningDate : String  = ""
)