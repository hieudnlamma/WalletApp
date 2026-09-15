package com.lmt.global.base.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lmt.global.base.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun findByPhoneNumber(phoneNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    fun observeByPhoneNumber(phoneNumber: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    @Query(
        "UPDATE users SET phoneNumber = :newPhoneNumber, fullName = :fullName, email = :email " +
            "WHERE phoneNumber = :currentPhoneNumber"
    )
    suspend fun updateProfile(
        currentPhoneNumber: String,
        newPhoneNumber: String,
        fullName: String?,
        email: String?,
    ): Int
}
