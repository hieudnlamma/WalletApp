package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipients",
    indices = [Index(value = ["normalizedName"], unique = true)]
)
data class RecipientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val normalizedName: String,
    val avatarKey: String,
    val lastTransferAt: Long
)
