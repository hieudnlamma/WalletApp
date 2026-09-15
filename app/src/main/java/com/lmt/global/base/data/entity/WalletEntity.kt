package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val userPhoneNumber: String,
    val balanceMinor: Long = INITIAL_BALANCE_MINOR,
) {
    companion object {
        const val INITIAL_BALANCE_MINOR = 1_423_500L
    }
}
