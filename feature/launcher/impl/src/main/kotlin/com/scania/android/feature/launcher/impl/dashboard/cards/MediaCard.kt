package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.core.car.model.MediaPlaybackState
import com.scania.android.core.car.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 媒体卡 UI State：完全由卡片自己定义，不会污染 Launcher 顶层状态。
 */
data class MediaCardUiState(
    val title: String = "没有正在播放的内容",
    val artist: String = "—",
    val album: String = "",
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
)

@HiltViewModel
class MediaCardViewModel @Inject constructor(
    private val repository: MediaRepository,
) : ViewModel() {

    val uiState: StateFlow<MediaCardUiState> = repository.playback
        .map { playback ->
            val duration = (playback.track?.durationMs ?: 0L).coerceAtLeast(1L)
            MediaCardUiState(
                title = playback.track?.title ?: "没有正在播放的内容",
                artist = playback.track?.artist ?: "—",
                album = playback.track?.album.orEmpty(),
                progress = (playback.positionMs.toFloat() / duration).coerceIn(0f, 1f),
                isPlaying = playback.state == MediaPlaybackState.Playing,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaCardUiState(),
        )

    fun togglePlay() {
        viewModelScope.launch {
            if (uiState.value.isPlaying) repository.pause() else repository.play()
        }
    }

    fun next() = viewModelScope.launch { repository.next() }
    fun previous() = viewModelScope.launch { repository.previous() }
}

/** Stateful 入口：被 [CardHost] 调用。 */
@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    viewModel: MediaCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MediaCardContent(
        state = state,
        onTogglePlay = viewModel::togglePlay,
        onNext = { viewModel.next() },
        onPrevious = { viewModel.previous() },
        modifier = modifier,
    )
}

/** Stateless 渲染层：纯函数，便于 Preview / 测试。 */
@Composable
fun MediaCardContent(
    state: MediaCardUiState,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "正在播放",
        subtitle = state.artist,
        modifier = modifier,
        accent = MaterialTheme.colorScheme.tertiary,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                )
                Text(
                    text = state.album,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, "上一首", modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = onTogglePlay) {
                    val icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                    Icon(icon, "播放/暂停", modifier = Modifier.size(48.dp))
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, "下一首", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
