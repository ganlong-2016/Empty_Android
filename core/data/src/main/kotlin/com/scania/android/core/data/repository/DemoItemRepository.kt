package com.scania.android.core.data.repository

import com.scania.android.core.model.DemoItem
import kotlinx.coroutines.flow.Flow

interface DemoItemRepository {
    fun observeItems(): Flow<List<DemoItem>>
    suspend fun refreshItems()
    suspend fun addItem(item: DemoItem)
    suspend fun removeItem(id: String)
}
