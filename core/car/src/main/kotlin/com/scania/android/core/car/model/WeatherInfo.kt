package com.scania.android.core.car.model

/**
 * 天气领域模型。真实项目中通常通过第三方 SDK / HTTP API 拿到。
 */
enum class WeatherCondition { Sunny, Cloudy, Rainy, Snowy, Windy, Thunder, Foggy, Unknown }

data class WeatherInfo(
    val city: String,
    val temperatureC: Int,
    val condition: WeatherCondition,
    val highC: Int,
    val lowC: Int,
    val humidityPercent: Int,
    val windSpeedKmH: Int,
    val updatedAtEpochMs: Long,
)
