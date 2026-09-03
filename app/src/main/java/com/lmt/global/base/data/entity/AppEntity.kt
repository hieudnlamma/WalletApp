package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = -1
)
