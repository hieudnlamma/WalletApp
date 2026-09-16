package com.lmt.global.base.data.repository.user

import com.lmt.global.base.data.dao.UserDao
import com.lmt.global.base.data.entity.UserEntity
import com.lmt.global.base.data.security.PasswordHasher
import com.lmt.global.base.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val passwordHasher: PasswordHasher,
) : UserRepository {

    override suspend fun findByPhoneNumber(phoneNumber: String): User? =
        userDao.findByPhoneNumber(phoneNumber)?.toModel()

    override fun observeByPhoneNumber(phoneNumber: String): Flow<User?> =
        userDao.observeByPhoneNumber(phoneNumber).map { it?.toModel() }

    override suspend fun add(user: User, passwordHash: String): Boolean {
        if (passwordHash.isBlank()) return false
        val entity = UserEntity.fromModel(
            user = user,
            passwordHash = passwordHash,
        )
        return userDao.insert(entity) != -1L
    }

    override suspend fun verifyPassword(
        phoneNumber: String,
        password: CharArray
    ): Boolean {
        val userEntity = userDao.findByPhoneNumber(phoneNumber)

        if (userEntity == null) {
            password.fill('\u0000')
            return false
        }

        return passwordHasher.verify(
            password = password,
            passwordHash = userEntity.passwordHash,
        )
    }

    override suspend fun updateProfile(
        currentPhoneNumber: String,
        user: User,
    ): Boolean {
        val currentEntity = userDao.findByPhoneNumber(currentPhoneNumber)
            ?: return false
        val updatedEntity = currentEntity.copy(
            phoneNumber = user.phoneNumber,
            fullName = user.fullName,
            email = user.email,
        )
        return userDao.updateProfileTransaction(
            currentPhoneNumber = currentPhoneNumber,
            user = updatedEntity,
        )
    }
}
