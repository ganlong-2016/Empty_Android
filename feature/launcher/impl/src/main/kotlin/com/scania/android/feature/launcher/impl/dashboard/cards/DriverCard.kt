package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.core.car.repository.DriverProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DriverCardUiState(
    val displayName: String = "—",
    val greetingHint: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class DriverCardViewModel @Inject constructor(
    repository: DriverProfileRepository,
) : ViewModel() {

    val uiState: StateFlow<DriverCardUiState> = repository.currentDriver
        .map {
            DriverCardUiState(
                displayName = it.displayName,
                greetingHint = it.greetingHint,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DriverCardUiState(),
        )
}

@Composable
fun DriverCard(
    modifier: Modifier = Modifier,
    viewModel: DriverCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DriverCardContent(state, modifier = modifier)
}

@Composable
fun DriverCardContent(
    state: DriverCardUiState,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "驾驶员",
        subtitle = state.displayName,
        modifier = modifier,
        accent = MaterialTheme.colorScheme.secondary,
    ) {
        if (state.isLoading) return@CardFrame
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = state.displayName,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = state.greetingHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
