package com.lmt.global.base.data.repository.user

import com.lmt.global.base.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun findByPhoneNumber(phoneNumber: String): User?

    fun observeByPhoneNumber(phoneNumber: String): Flow<User?>

    suspend fun add(user: User): Boolean

    suspend fun updateProfile(
        currentPhoneNumber: String,
        user: User,
    ): Boolean
}
