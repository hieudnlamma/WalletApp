package com.lmt.global.base.model

data class User(
    val phoneNumber: String,
    val fullName: String? = null,
    val email: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
