package com.scania.android.feature.launcher.impl.dashboard.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scania.android.core.car.model.VehicleStatus
import com.scania.android.core.car.repository.VehicleStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class VehicleCardUiState(
    val batteryPercent: Int = 0,
    val rangeKm: Int = 0,
    val odometerKm: Int = 0,
    val outsideTempC: Int = 0,
    val doorsLocked: Boolean = true,
    val gear: String = "P",
    val isLoading: Boolean = true,
)

@HiltViewModel
class VehicleCardViewModel @Inject constructor(
    repository: VehicleStatusRepository,
) : ViewModel() {

    val uiState: StateFlow<VehicleCardUiState> = repository.status
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VehicleCardUiState(),
        )

    private fun VehicleStatus.toUiState() = VehicleCardUiState(
        batteryPercent = batteryPercent,
        rangeKm = rangeKm,
        odometerKm = odometerKm,
        outsideTempC = outsideTempC,
        doorsLocked = doorsLocked,
        gear = gear.name,
        isLoading = false,
    )
}

@Composable
fun VehicleCard(
    modifier: Modifier = Modifier,
    viewModel: VehicleCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VehicleCardContent(state, modifier = modifier)
}

@Composable
fun VehicleCardContent(
    state: VehicleCardUiState,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "车辆状态",
        subtitle = "${state.rangeKm} km 续航",
        modifier = modifier,
        accent = MaterialTheme.colorScheme.tertiary,
    ) {
        if (state.isLoading) return@CardFrame
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "电量", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${state.batteryPercent}%", style = MaterialTheme.typography.titleMedium)
            }
            LinearProgressIndicator(
                progress = { state.batteryPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledValue("车外温度", "${state.outsideTempC}°C")
                LabeledValue("档位", state.gear)
                LabeledValue("车门", if (state.doorsLocked) "已锁" else "未锁")
            }
            LabeledValue("里程", "${state.odometerKm} km")
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}
