package com.scania.android.core.car.model

/**
 * 车机当前用户（驾驶员）信息。
 *
 * 通常通过账号三方 SDK（如 Scania ID / OEM Account SDK）或车机 IPC 获取。
 */
data class DriverProfile(
    val id: String,
    val displayName: String,
    val avatarUrl: String?,
    val greetingHint: String,
)
