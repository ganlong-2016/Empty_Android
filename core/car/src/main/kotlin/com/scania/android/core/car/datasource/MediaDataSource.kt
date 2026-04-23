package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.MediaPlayback
import kotlinx.coroutines.flow.Flow

/**
 * 媒体数据源抽象。
 *
 * 真实车机上应由 AIDL 实现（见 `IMediaSessionService.aidl`），Launcher 进程中走 Binder 调用。
 * 在开发/预览环境下使用 [FakeMediaDataSource] 填充本地 demo 数据。
 */
interface MediaDataSource {
    val playback: Flow<MediaPlayback>

    suspend fun play()
    suspend fun pause()
    suspend fun next()
    suspend fun previous()
}
