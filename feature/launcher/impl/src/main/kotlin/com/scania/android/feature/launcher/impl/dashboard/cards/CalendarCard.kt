package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.scania.android.core.car.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CalendarCardUiState(
    val events: List<Entry> = emptyList(),
) {
    data class Entry(
        val id: String,
        val title: String,
        val timeRange: String,
    )
}

@HiltViewModel
class CalendarCardViewModel @Inject constructor(
    repository: CalendarRepository,
) : ViewModel() {

    val uiState: StateFlow<CalendarCardUiState> = repository.events
        .map { events ->
            CalendarCardUiState(
                events = events.map {
                    CalendarCardUiState.Entry(
                        id = it.id,
                        title = it.title,
                        timeRange = "${timeFmt.format(Date(it.startEpochMs))} - ${timeFmt.format(Date(it.endEpochMs))}",
                    )
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarCardUiState(),
        )

    companion object {
        private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    }
}

@Composable
fun CalendarCard(
    modifier: Modifier = Modifier,
    viewModel: CalendarCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarCardContent(state, modifier = modifier)
}

@Composable
fun CalendarCardContent(
    state: CalendarCardUiState,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "日程",
        subtitle = "今天 ${state.events.size} 项",
        modifier = modifier,
        accent = MaterialTheme.colorScheme.primary,
    ) {
        if (state.events.isEmpty()) {
            Text(
                text = "今日无日程",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@CardFrame
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.events, key = { it.id }) { event ->
                EventRow(event)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun EventRow(entry: CalendarCardUiState.Entry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Event,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = entry.timeRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
