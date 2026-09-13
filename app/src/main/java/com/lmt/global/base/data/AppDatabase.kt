package com.lmt.global.base.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lmt.global.base.data.dao.WalletDao
import com.lmt.global.base.data.dao.UserDao
import com.lmt.global.base.data.entity.RecipientEntity
import com.lmt.global.base.data.entity.TransactionEntity
import com.lmt.global.base.data.entity.WalletEntity
import com.lmt.global.base.data.entity.CardEntity
import com.lmt.global.base.data.entity.UserEntity

@Database(
    entities = [
        WalletEntity::class,
        RecipientEntity::class,
        TransactionEntity::class,
        CardEntity::class,
        UserEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "wallet.db"
    }
}
