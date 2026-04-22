package com.scania.android.core.car.di

import com.scania.android.core.car.datasource.CalendarDataSource
import com.scania.android.core.car.datasource.DriverProfileDataSource
import com.scania.android.core.car.datasource.FakeCalendarDataSource
import com.scania.android.core.car.datasource.FakeDriverProfileDataSource
import com.scania.android.core.car.datasource.FakeMediaDataSource
import com.scania.android.core.car.datasource.FakeVehicleStatusDataSource
import com.scania.android.core.car.datasource.FakeWeatherDataSource
import com.scania.android.core.car.datasource.MediaDataSource
import com.scania.android.core.car.datasource.VehicleStatusDataSource
import com.scania.android.core.car.datasource.WeatherDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 把 Fake 实现注入到接口。
 *
 * 真实接入 AIDL / 三方 SDK 时，新增对应的 `RealXxxDataSource` 并把 `@Binds` 切换过去即可，
 * 业务层与 UI 层不需要任何改动。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CarBindings {

    @Binds
    @Singleton
    abstract fun bindMediaDataSource(impl: FakeMediaDataSource): MediaDataSource

    @Binds
    @Singleton
    abstract fun bindWeatherDataSource(impl: FakeWeatherDataSource): WeatherDataSource

    @Binds
    @Singleton
    abstract fun bindCalendarDataSource(impl: FakeCalendarDataSource): CalendarDataSource

    @Binds
    @Singleton
    abstract fun bindDriverProfileDataSource(
        impl: FakeDriverProfileDataSource,
    ): DriverProfileDataSource

    @Binds
    @Singleton
    abstract fun bindVehicleStatusDataSource(
        impl: FakeVehicleStatusDataSource,
    ): VehicleStatusDataSource
}
