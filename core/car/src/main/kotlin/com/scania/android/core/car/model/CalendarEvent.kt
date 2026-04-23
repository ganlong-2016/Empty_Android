package com.scania.android.core.car.model

/**
 * 驾驶员日程事件（从 Calendar Provider / 账号 SDK 聚合得到）。
 */
data class CalendarEvent(
    val id: String,
    val title: String,
    val location: String?,
    val startEpochMs: Long,
    val endEpochMs: Long,
    val isAllDay: Boolean,
)
