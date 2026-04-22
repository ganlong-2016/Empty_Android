package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.DriverProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface DriverProfileDataSource {
    val currentDriver: Flow<DriverProfile>
}

@Singleton
class FakeDriverProfileDataSource @Inject constructor() : DriverProfileDataSource {
    override val currentDriver: Flow<DriverProfile> = MutableStateFlow(
        DriverProfile(
            id = "driver_001",
            displayName = "Alex",
            avatarUrl = null,
            greetingHint = "欢迎回到 Scania，今天也一路平安",
        ),
    )
}
