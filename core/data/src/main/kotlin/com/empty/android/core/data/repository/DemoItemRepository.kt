package com.empty.android.core.data.repository

import com.empty.android.core.model.DemoItem
import kotlinx.coroutines.flow.Flow

interface DemoItemRepository {
    fun observeItems(): Flow<List<DemoItem>>
    suspend fun refreshItems()
    suspend fun addItem(item: DemoItem)
    suspend fun removeItem(id: String)
}
