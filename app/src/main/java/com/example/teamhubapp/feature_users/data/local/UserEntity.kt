
package com.example.teamhubapp.feature_users.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room database table — mirrors User domain model
// Never use this outside data layer
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id          : String  = "",
    val name        : String  = "",
    val email       : String  = "",
    val designation : String  = "",
    val department  : String  = "",
    val isActive: Boolean? = null  ,
    val imageUrl    : String  = "",
    val city        : String  = "",
    val country     : String  = "",
    val joiningDate : String  = ""
)