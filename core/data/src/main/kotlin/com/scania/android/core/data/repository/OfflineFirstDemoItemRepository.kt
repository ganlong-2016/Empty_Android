package com.scania.android.core.data.repository

import com.scania.android.core.common.network.AppDispatcher
import com.scania.android.core.common.network.Dispatcher
import com.scania.android.core.database.dao.DemoItemDao
import com.scania.android.core.database.model.asEntity
import com.scania.android.core.database.model.asExternalModel
import com.scania.android.core.model.DemoItem
import com.scania.android.core.network.DemoApi
import com.scania.android.core.network.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 示例仓库实现：Offline-First 风格——以本地数据库为唯一真实来源，远程数据仅用于更新缓存。
 *
 * 真实业务场景中可以：
 * 1. 增加网络失败时的重试；
 * 2. 使用 [com.scania.android.core.common.result.Result] 暴露加载/错误状态；
 * 3. 引入同步服务 (WorkManager) 做后台同步。
 */
@Singleton
class OfflineFirstDemoItemRepository @Inject constructor(
    private val demoItemDao: DemoItemDao,
    private val demoApi: DemoApi,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : DemoItemRepository {

    override fun observeItems(): Flow<List<DemoItem>> =
        demoItemDao.observeAll()
            .map { list -> list.map { it.asExternalModel() } }
            .flowOn(ioDispatcher)

    override suspend fun refreshItems() = withContext(ioDispatcher) {
        val remote = runCatching { demoApi.getDemoItems() }.getOrNull().orEmpty()
        if (remote.isNotEmpty()) {
            demoItemDao.upsertAll(remote.map { it.asExternalModel().asEntity() })
        }
    }

    override suspend fun addItem(item: DemoItem) = withContext(ioDispatcher) {
        demoItemDao.upsertAll(listOf(item.asEntity()))
    }

    override suspend fun removeItem(id: String) = withContext(ioDispatcher) {
        demoItemDao.deleteById(id)
    }
}
