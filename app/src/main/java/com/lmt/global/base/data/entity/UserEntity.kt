package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lmt.global.base.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val phoneNumber: String,
    val fullName: String? = null,
    val email: String? = null,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun toModel() = User(
        phoneNumber = phoneNumber,
        fullName = fullName,
        email = email,
        createdAt = createdAt,
    )

    companion object {
        fun fromModel(user: User, passwordHash: String) = UserEntity(
            phoneNumber = user.phoneNumber,
            fullName = user.fullName,
            email = user.email,
            passwordHash = passwordHash,
            createdAt = user.createdAt,
        )
    }
}
