package com.lmt.global.base.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `cards` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `cardNumber` TEXT NOT NULL,
                    `balanceMinor` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_cards_cardNumber` ON `cards` (`cardNumber`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_cards_createdAt` ON `cards` (`createdAt`)"
            )
            db.execSQL("ALTER TABLE `transactions` ADD COLUMN `billerType` TEXT")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `users` (
                    `phoneNumber` TEXT NOT NULL,
                    `fullName` TEXT,
                    `email` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    PRIMARY KEY(`phoneNumber`)
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `transactions` ADD COLUMN `currencyCode` TEXT NOT NULL DEFAULT 'USD'"
            )
            db.execSQL("ALTER TABLE `transactions` ADD COLUMN `dueText` TEXT")
            db.execSQL("ALTER TABLE `transactions` ADD COLUMN `category` TEXT")
            db.execSQL("ALTER TABLE `transactions` ADD COLUMN `dueDate` TEXT")
            db.execSQL("ALTER TABLE `transactions` ADD COLUMN `registrationNumber` TEXT")
            db.execSQL("UPDATE `transactions` SET `category` = `billerType` WHERE `billerType` IS NOT NULL")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `wallet_new` (
                    `userPhoneNumber` TEXT NOT NULL,
                    `balanceMinor` INTEGER NOT NULL,
                    PRIMARY KEY(`userPhoneNumber`)
                )
                """.trimIndent()
            )
            db.execSQL(
                "INSERT INTO `wallet_new` (`userPhoneNumber`, `balanceMinor`) " +
                    "SELECT '__legacy_user__', `balanceMinor` FROM `wallet`"
            )
            db.execSQL("DROP TABLE `wallet`")
            db.execSQL("ALTER TABLE `wallet_new` RENAME TO `wallet`")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `transactions_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `type` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `recipientId` INTEGER,
                    `iconKey` TEXT NOT NULL,
                    `billerType` TEXT,
                    `amountMinor` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `currencyCode` TEXT NOT NULL,
                    `dueText` TEXT,
                    `category` TEXT,
                    `dueDate` TEXT,
                    `registrationNumber` TEXT,
                    `userPhoneNumber` TEXT NOT NULL,
                    FOREIGN KEY(`recipientId`) REFERENCES `recipients`(`id`)
                        ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `transactions_new` (
                    `id`, `type`, `title`, `recipientId`, `iconKey`, `billerType`,
                    `amountMinor`, `createdAt`, `currencyCode`, `dueText`, `category`,
                    `dueDate`, `registrationNumber`, `userPhoneNumber`
                )
                SELECT
                    `id`, `type`, `title`, `recipientId`, `iconKey`, `billerType`,
                    `amountMinor`, `createdAt`, `currencyCode`, `dueText`, `category`,
                    `dueDate`, `registrationNumber`, '__legacy_user__'
                FROM `transactions`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `transactions`")
            db.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_transactions_recipientId` " +
                    "ON `transactions` (`recipientId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_transactions_createdAt` " +
                    "ON `transactions` (`createdAt`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_transactions_userPhoneNumber` " +
                    "ON `transactions` (`userPhoneNumber`)"
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `recipients` ADD COLUMN `phoneNumber` TEXT NOT NULL DEFAULT ''"
            )
        }
    }
}
