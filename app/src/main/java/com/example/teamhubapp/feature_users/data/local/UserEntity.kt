package com.example.teamhubapp.feature_users.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val id: String,

    val name: String,
    val designation: String,
    val department: String,
    val isActive: Boolean,
    val email: String,
    val city: String,
    val country: String,
    val joiningDate: String
)