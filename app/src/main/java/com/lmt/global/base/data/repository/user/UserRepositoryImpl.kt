package com.lmt.global.base.data.repository.user

import com.lmt.global.base.data.dao.UserDao
import com.lmt.global.base.data.entity.UserEntity
import com.lmt.global.base.model.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun findByPhoneNumber(phoneNumber: String): User? =
        userDao.findByPhoneNumber(phoneNumber)?.toModel()

    override suspend fun add(user: User): Boolean =
        userDao.insert(UserEntity.fromModel(user)) != -1L
}
