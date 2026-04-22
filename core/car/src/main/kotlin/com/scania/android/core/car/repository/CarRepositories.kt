package com.scania.android.core.car.repository

import com.scania.android.core.car.datasource.CalendarDataSource
import com.scania.android.core.car.datasource.DriverProfileDataSource
import com.scania.android.core.car.datasource.MediaDataSource
import com.scania.android.core.car.datasource.VehicleStatusDataSource
import com.scania.android.core.car.datasource.WeatherDataSource
import com.scania.android.core.car.model.CalendarEvent
import com.scania.android.core.car.model.DriverProfile
import com.scania.android.core.car.model.MediaPlayback
import com.scania.android.core.car.model.VehicleStatus
import com.scania.android.core.car.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 这一层只是把数据源「再抽一道」给 feature 层用，
 * 后续可以加缓存、重试、聚合、多源融合等逻辑。
 */
@Singleton
class MediaRepository @Inject constructor(
    private val source: MediaDataSource,
) {
    val playback: Flow<MediaPlayback> = source.playback
    suspend fun play() = source.play()
    suspend fun pause() = source.pause()
    suspend fun next() = source.next()
    suspend fun previous() = source.previous()
}

@Singleton
class WeatherRepository @Inject constructor(
    private val source: WeatherDataSource,
) {
    val current: Flow<WeatherInfo> = source.current
    suspend fun refresh() = source.refresh()
}

@Singleton
class CalendarRepository @Inject constructor(
    private val source: CalendarDataSource,
) {
    val events: Flow<List<CalendarEvent>> = source.events
}

@Singleton
class DriverProfileRepository @Inject constructor(
    private val source: DriverProfileDataSource,
) {
    val currentDriver: Flow<DriverProfile> = source.currentDriver
}

@Singleton
class VehicleStatusRepository @Inject constructor(
    private val source: VehicleStatusDataSource,
) {
    val status: Flow<VehicleStatus> = source.status
}
