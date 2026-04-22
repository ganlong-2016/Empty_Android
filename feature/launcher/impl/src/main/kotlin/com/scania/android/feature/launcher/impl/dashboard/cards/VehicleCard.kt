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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scania.android.core.car.model.VehicleStatus

@Composable
fun VehicleCard(
    status: VehicleStatus?,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "车辆状态",
        subtitle = status?.let { "${it.rangeKm} km 续航" } ?: "—",
        modifier = modifier,
        accent = MaterialTheme.colorScheme.tertiary,
    ) {
        if (status == null) return@CardFrame
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "电量", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${status.batteryPercent}%", style = MaterialTheme.typography.titleMedium)
            }
            LinearProgressIndicator(
                progress = { status.batteryPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledValue("车外温度", "${status.outsideTempC}°C")
                LabeledValue("档位", status.gear.name)
                LabeledValue("车门", if (status.doorsLocked) "已锁" else "未锁")
            }
            LabeledValue("里程", "${status.odometerKm} km")
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
