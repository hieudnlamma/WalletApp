package com.lmt.global.base.data.repository.user

import com.lmt.global.base.model.User

interface UserRepository {

    suspend fun findByPhoneNumber(phoneNumber: String): User?

    suspend fun add(user: User): Boolean
}
