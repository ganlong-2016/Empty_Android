package com.empty.android.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.empty.android.core.database.dao.DemoItemDao
import com.empty.android.core.database.model.DemoItemEntity

@Database(
    entities = [DemoItemEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun demoItemDao(): DemoItemDao
}
