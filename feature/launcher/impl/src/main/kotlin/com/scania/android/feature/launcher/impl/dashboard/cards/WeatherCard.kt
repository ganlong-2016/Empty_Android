package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.core.car.model.WeatherCondition
import com.scania.android.core.car.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class WeatherCardUiState(
    val city: String = "—",
    val temperatureC: Int = 0,
    val condition: WeatherCondition = WeatherCondition.Unknown,
    val highC: Int = 0,
    val lowC: Int = 0,
    val humidityPercent: Int = 0,
    val windSpeedKmH: Int = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class WeatherCardViewModel @Inject constructor(
    repository: WeatherRepository,
) : ViewModel() {

    val uiState: StateFlow<WeatherCardUiState> = repository.current
        .map {
            WeatherCardUiState(
                city = it.city,
                temperatureC = it.temperatureC,
                condition = it.condition,
                highC = it.highC,
                lowC = it.lowC,
                humidityPercent = it.humidityPercent,
                windSpeedKmH = it.windSpeedKmH,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeatherCardUiState(),
        )
}

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    viewModel: WeatherCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherCardContent(state = state, modifier = modifier)
}

@Composable
fun WeatherCardContent(
    state: WeatherCardUiState,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "天气",
        subtitle = state.city,
        modifier = modifier,
        accent = MaterialTheme.colorScheme.secondary,
    ) {
        if (state.isLoading) {
            Text(
                text = "天气数据加载中…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@CardFrame
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = state.condition.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(64.dp),
                )
                Text(
                    text = "${state.temperatureC}°",
                    style = MaterialTheme.typography.displayMedium,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${state.highC}° / ${state.lowC}°",
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "湿度 ${state.humidityPercent}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "风速 ${state.windSpeedKmH} km/h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun WeatherCondition.icon(): ImageVector = when (this) {
    WeatherCondition.Sunny -> Icons.Default.WbSunny
    WeatherCondition.Cloudy -> Icons.Default.Cloud
    WeatherCondition.Rainy -> Icons.Default.Grain
    WeatherCondition.Snowy -> Icons.Default.AcUnit
    WeatherCondition.Windy -> Icons.Default.Air
    WeatherCondition.Thunder -> Icons.Default.Thunderstorm
    WeatherCondition.Foggy -> Icons.Default.Cloud
    WeatherCondition.Unknown -> Icons.Default.Cloud
}
