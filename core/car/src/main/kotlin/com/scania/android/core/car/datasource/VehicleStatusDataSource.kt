package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.VehicleStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface VehicleStatusDataSource {
    val status: Flow<VehicleStatus>
}

@Singleton
class FakeVehicleStatusDataSource @Inject constructor() : VehicleStatusDataSource {
    override val status: Flow<VehicleStatus> = MutableStateFlow(
        VehicleStatus(
            batteryPercent = 78,
            rangeKm = 412,
            fuelPercent = null,
            odometerKm = 124_580,
            outsideTempC = -1,
            doorsLocked = true,
            gear = VehicleStatus.Gear.P,
        ),
    )
}
