package com.example.teamhubapp.feature_users.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Continuous stream — auto-updates UI when DB changes
    @Query("SELECT * FROM users")
    fun observeUsers(): Flow<List<UserEntity>>

    // Replace existing user if id conflicts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    // Wipe all users before fresh insert
    @Query("DELETE FROM users")
    suspend fun clearUsers()

    // Single user stream for detail screen
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>
}
