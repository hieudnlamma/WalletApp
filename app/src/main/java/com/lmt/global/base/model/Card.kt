package com.lmt.global.base.model

import androidx.room.PrimaryKey

data class Card(
    val id: String,
    val name: String,
    val cardNumber: String,
    val balanceMinor: Long,
    val createdAt: Long
)
