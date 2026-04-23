package com.scania.android.feature.launcher.api

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

/**
 * Launcher feature 的根路由 —— 车机开机后第一个可见页。
 *
 * 内部通过 Compose 的 HorizontalPager 同时承载「3D 全屏展示页」和「卡片主页」，
 * 所以对 app 模块来说只需要一个路由 key。
 */
@Serializable
data object LauncherRoute

fun NavController.navigateToLauncher(navOptions: NavOptions? = null) =
    navigate(route = LauncherRoute, navOptions = navOptions)
