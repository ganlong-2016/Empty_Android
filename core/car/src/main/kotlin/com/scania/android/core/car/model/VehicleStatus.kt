package com.scania.android.core.car.model

/**
 * 车辆核心状态信息（电量、续航、胎压等），真实数据来自 Car Service / VehicleHAL AIDL。
 */
data class VehicleStatus(
    val batteryPercent: Int,
    val rangeKm: Int,
    val fuelPercent: Int?,
    val odometerKm: Int,
    val outsideTempC: Int,
    val doorsLocked: Boolean,
    val gear: Gear,
) {
    enum class Gear { P, R, N, D, Unknown }
}
