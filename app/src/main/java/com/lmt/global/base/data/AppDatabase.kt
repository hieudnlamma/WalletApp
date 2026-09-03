package com.lmt.global.base.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lmt.global.base.data.entity.AppEntity

@Database(
    entities = [
        AppEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "phonetracker.db"
    }
}
