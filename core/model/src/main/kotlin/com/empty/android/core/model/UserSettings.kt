package com.empty.android.core.model

/**
 * 用户偏好设置数据：作为持久化到 DataStore 的基础示例。
 */
data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}
