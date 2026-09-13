package com.lmt.global.base.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = SINGLE_WALLET_ID,
    val balanceMinor: Long = 0L
) {
    companion object {
        const val SINGLE_WALLET_ID = 1
    }
}
