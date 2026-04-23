package com.scania.android.feature.home.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scania.android.feature.home.api.HomeRoute
import com.scania.android.feature.home.impl.HomeScreen

/**
 * Home feature 向导航图注册自己的 composable。
 *
 * [onNavigateToSettings] 由 `:app` 传入：`:app` 依赖了 `feature:settings:api` 以获取
 * `navigateToSettings` 扩展函数，并把它作为回调传进来，从而解耦 `feature:home` 与 `feature:settings`
 * 的 impl。
 */
fun NavGraphBuilder.homeScreen(
    onNavigateToSettings: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(onNavigateToSettings = onNavigateToSettings)
    }
}
