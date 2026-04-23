package com.scania.android.core.car.model

/**
 * 媒体播放状态（跨 feature 共享的领域模型）。
 */
enum class MediaPlaybackState {
    Stopped,
    Playing,
    Paused,
    Buffering,
    Error,
    ;

    companion object {
        fun fromAidl(state: Int): MediaPlaybackState = when (state) {
            1 -> Playing
            2 -> Paused
            3 -> Buffering
            4 -> Error
            else -> Stopped
        }
    }
}

data class MediaTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverUri: String?,
)

data class MediaPlayback(
    val track: MediaTrack?,
    val state: MediaPlaybackState,
    val positionMs: Long,
)
