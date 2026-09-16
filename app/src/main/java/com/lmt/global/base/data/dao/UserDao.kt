package com.lmt.global.base.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query(
        "UPDATE wallet SET userPhoneNumber = :newPhoneNumber " +
                "WHERE userPhoneNumber = :currentPhoneNumber"
    )
    suspend fun updateWalletOwner(
        currentPhoneNumber: String,
        newPhoneNumber: String,
    ): Int

    @Query(
        "UPDATE transactions SET userPhoneNumber = :newPhoneNumber " +
                "WHERE userPhoneNumber = :currentPhoneNumber"
    )
    suspend fun updateTransactionOwner(
        currentPhoneNumber: String,
        newPhoneNumber: String,
    ): Int

    @Transaction
    suspend fun updateProfileTransaction(
        currentPhoneNumber: String,
        user: UserEntity,
    ): Boolean {
        val isChangingPhoneNumber =
            user.phoneNumber != currentPhoneNumber

        if (
            isChangingPhoneNumber &&
            findByPhoneNumber(user.phoneNumber) != null
        ) {
            return false
        }

        val updatedRows = updateProfile(
            currentPhoneNumber = currentPhoneNumber,
            newPhoneNumber = user.phoneNumber,
            fullName = user.fullName,
            email = user.email,
        )

        if (updatedRows == 0) {
            return false
        }

        if (isChangingPhoneNumber) {
            updateWalletOwner(
                currentPhoneNumber = currentPhoneNumber,
                newPhoneNumber = user.phoneNumber,
            )

            updateTransactionOwner(
                currentPhoneNumber = currentPhoneNumber,
                newPhoneNumber = user.phoneNumber,
            )
        }

        return true
    }
}
