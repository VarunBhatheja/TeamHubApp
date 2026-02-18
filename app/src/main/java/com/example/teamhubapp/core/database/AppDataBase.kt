package com.example.teamhubapp.core.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teamhubapp.feature_users.data.local.UserDao
import com.example.teamhubapp.feature_users.data.local.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
