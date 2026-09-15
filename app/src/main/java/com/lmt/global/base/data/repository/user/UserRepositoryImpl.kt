package com.lmt.global.base.data.repository.user

import androidx.room.withTransaction
import com.lmt.global.base.data.AppDatabase
import com.lmt.global.base.data.entity.UserEntity
import com.lmt.global.base.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(private val database: AppDatabase) : UserRepository {
    private val userDao = database.userDao()
    private val walletDao = database.walletDao()

    override suspend fun findByPhoneNumber(phoneNumber: String): User? =
        userDao.findByPhoneNumber(phoneNumber)?.toModel()

    override fun observeByPhoneNumber(phoneNumber: String): Flow<User?> =
        userDao.observeByPhoneNumber(phoneNumber).map { it?.toModel() }

    override suspend fun add(user: User): Boolean =
        userDao.insert(UserEntity.fromModel(user)) != -1L

    override suspend fun updateProfile(
        currentPhoneNumber: String,
        user: User,
    ): Boolean = database.withTransaction {
        if (
            user.phoneNumber != currentPhoneNumber &&
            userDao.findByPhoneNumber(user.phoneNumber) != null
        ) {
            return@withTransaction false
        }

        val updated = userDao.updateProfile(
            currentPhoneNumber = currentPhoneNumber,
            newPhoneNumber = user.phoneNumber,
            fullName = user.fullName,
            email = user.email,
        )
        if (updated == 0) return@withTransaction false

        if (user.phoneNumber != currentPhoneNumber) {
            walletDao.updateWalletOwner(currentPhoneNumber, user.phoneNumber)
            walletDao.updateTransactionOwner(currentPhoneNumber, user.phoneNumber)
        }
        true
    }
}
