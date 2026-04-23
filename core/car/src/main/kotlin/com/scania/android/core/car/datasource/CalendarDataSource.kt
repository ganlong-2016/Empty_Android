package com.scania.android.core.car.datasource

import com.scania.android.core.car.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface CalendarDataSource {
    val events: Flow<List<CalendarEvent>>
}

@Singleton
class FakeCalendarDataSource @Inject constructor() : CalendarDataSource {

    private val now = System.currentTimeMillis()
    private val demo = listOf(
        CalendarEvent(
            id = "1",
            title = "Pickup at Södertälje Terminal",
            location = "Scania HQ",
            startEpochMs = now + 30 * 60_000L,
            endEpochMs = now + 90 * 60_000L,
            isAllDay = false,
        ),
        CalendarEvent(
            id = "2",
            title = "Driver shift briefing",
            location = "Depot A",
            startEpochMs = now + 3 * 60 * 60_000L,
            endEpochMs = now + 3 * 60 * 60_000L + 30 * 60_000L,
            isAllDay = false,
        ),
        CalendarEvent(
            id = "3",
            title = "Route review",
            location = null,
            startEpochMs = now + 6 * 60 * 60_000L,
            endEpochMs = now + 7 * 60 * 60_000L,
            isAllDay = false,
        ),
    )

    override val events: Flow<List<CalendarEvent>> = MutableStateFlow(demo)
}
