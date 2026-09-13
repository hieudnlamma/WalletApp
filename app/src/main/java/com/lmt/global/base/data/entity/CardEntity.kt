package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [Index(value = ["cardNumber"], unique = true), Index("createdAt")]
)
data class CardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cardNumber: String,
    val balanceMinor: Long,
    val createdAt: Long
)
