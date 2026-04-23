package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.WeatherCondition
import com.scania.android.core.car.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface WeatherDataSource {
    val current: Flow<WeatherInfo>
    suspend fun refresh()
}

/**
 * Fake 天气实现。真实集成一般接入 OEM 提供的天气 SDK 或地图厂商 SDK。
 */
@Singleton
class FakeWeatherDataSource @Inject constructor() : WeatherDataSource {

    private val _current = MutableStateFlow(
        WeatherInfo(
            city = "Stockholm",
            temperatureC = -2,
            condition = WeatherCondition.Snowy,
            highC = 1,
            lowC = -6,
            humidityPercent = 72,
            windSpeedKmH = 14,
            updatedAtEpochMs = System.currentTimeMillis(),
        ),
    )

    override val current: Flow<WeatherInfo> = _current

    override suspend fun refresh() {
        _current.value = _current.value.copy(updatedAtEpochMs = System.currentTimeMillis())
    }
}
