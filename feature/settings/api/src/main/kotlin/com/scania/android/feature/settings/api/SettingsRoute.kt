package com.scania.android.feature.settings.api

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

/**
 * 设置页的导航 key。
 *
 * 其他 feature 需要跳转到设置页时，仅依赖 `:feature:settings:api` 即可。
 */
@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions = navOptions)
