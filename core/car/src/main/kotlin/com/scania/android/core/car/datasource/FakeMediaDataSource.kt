package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.MediaPlayback
import com.scania.android.core.car.model.MediaPlaybackState
import com.scania.android.core.car.model.MediaTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Launcher 预览/开发时使用的本地假实现。
 *
 * 当真实车机侧 `IMediaSessionService` 接入后，只需要替换 [com.scania.android.core.car.di.CarBindings]
 * 里的 binding，即可让整个应用切换到 AIDL 数据源而不修改业务代码。
 */
@Singleton
class FakeMediaDataSource @Inject constructor() : MediaDataSource {

    private val playlist = listOf(
        MediaTrack(
            id = "1",
            title = "Highway Cruise",
            artist = "Scania Drive Radio",
            album = "On The Road vol.1",
            durationMs = 213_000,
            coverUri = null,
        ),
        MediaTrack(
            id = "2",
            title = "Night Shift",
            artist = "Truckers FM",
            album = "Night Sessions",
            durationMs = 187_000,
            coverUri = null,
        ),
        MediaTrack(
            id = "3",
            title = "Nordic Winter",
            artist = "Aurora Lights",
            album = "Aurora",
            durationMs = 242_000,
            coverUri = null,
        ),
    )
    private var index = 0

    private val _playback = MutableStateFlow(
        MediaPlayback(
            track = playlist[0],
            state = MediaPlaybackState.Paused,
            positionMs = 0L,
        ),
    )

    override val playback: StateFlow<MediaPlayback> = _playback.asStateFlow()

    override suspend fun play() {
        _playback.update { it.copy(state = MediaPlaybackState.Playing) }
    }

    override suspend fun pause() {
        _playback.update { it.copy(state = MediaPlaybackState.Paused) }
    }

    override suspend fun next() {
        index = (index + 1) % playlist.size
        _playback.update { MediaPlayback(playlist[index], MediaPlaybackState.Playing, 0L) }
    }

    override suspend fun previous() {
        index = (index - 1 + playlist.size) % playlist.size
        _playback.update { MediaPlayback(playlist[index], MediaPlaybackState.Playing, 0L) }
    }
}
