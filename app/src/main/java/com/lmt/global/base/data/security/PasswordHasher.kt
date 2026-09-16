package com.lmt.global.base.data.security

import at.favre.lib.crypto.bcrypt.BCrypt

interface PasswordHasher {

    fun hash(password: CharArray): String

    fun verify(
        password: CharArray,
        passwordHash: String,
    ): Boolean
}

class BcryptPasswordHasher : PasswordHasher {

    override fun hash(password: CharArray): String {
        return try {
            BCrypt.withDefaults().hashToString(
                BCRYPT_COST,
                password,
            )
        } finally {
            password.fill('\u0000')
        }
    }

    override fun verify(
        password: CharArray,
        passwordHash: String,
    ): Boolean {
        return try {
            if (passwordHash.isBlank()) {
                false
            } else {
                BCrypt.verifyer()
                    .verify(password, passwordHash)
                    .verified
            }
        } catch (_: IllegalArgumentException) {
            false
        } finally {
            password.fill('\u0000')
        }
    }

    private companion object {
        const val BCRYPT_COST = 12
    }
}