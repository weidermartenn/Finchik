package com.example.finance.User

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}