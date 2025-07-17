package com.henrypeya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.henrypeya.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity): Int

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String): Int

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}