package com.empty.android.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.empty.android.core.database.model.DemoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DemoItemDao {

    @Query("SELECT * FROM demo_items ORDER BY title ASC")
    fun observeAll(): Flow<List<DemoItemEntity>>

    @Query("SELECT * FROM demo_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DemoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DemoItemEntity>)

    @Query("DELETE FROM demo_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
