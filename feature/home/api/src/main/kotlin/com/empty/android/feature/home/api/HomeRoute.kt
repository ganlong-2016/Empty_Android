package com.empty.android.feature.home.api

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

/**
 * Home 页的导航 key。
 *
 * 暴露在 `:api` 模块，其他 feature 的 `:impl` 如果需要跳转到 Home，
 * 仅依赖本 `:api` 模块即可获取到该 key 以及 [navigateToHome]。
 */
@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions = navOptions)
