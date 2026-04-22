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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.scania.android.core.car.model.WeatherCondition
import com.scania.android.core.car.model.WeatherInfo

@Composable
fun WeatherCard(
    weather: WeatherInfo?,
    modifier: Modifier = Modifier,
) {
    CardFrame(
        title = "天气",
        subtitle = weather?.city ?: "—",
        modifier = modifier,
        accent = MaterialTheme.colorScheme.secondary,
    ) {
        if (weather == null) {
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
                    imageVector = weather.condition.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(64.dp),
                )
                Text(
                    text = "${weather.temperatureC}°",
                    style = MaterialTheme.typography.displayMedium,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${weather.highC}° / ${weather.lowC}°",
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "湿度 ${weather.humidityPercent}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "风速 ${weather.windSpeedKmH} km/h",
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
