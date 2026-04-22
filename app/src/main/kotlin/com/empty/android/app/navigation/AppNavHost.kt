package com.empty.android.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.empty.android.feature.home.api.HomeRoute
import com.empty.android.feature.home.impl.navigation.homeScreen
import com.empty.android.feature.settings.api.navigateToSettings
import com.empty.android.feature.settings.impl.navigation.settingsScreen

/**
 * App 级导航图：
 * - 通过 `feature:*:api` 拿到每个 feature 的路由 key 和跳转扩展；
 * - 通过 `feature:*:impl` 的 `xxxScreen()` 扩展把 Composable 挂到导航图上。
 *
 * 这样 app 层不需要知道每个 feature 的实现细节，而 feature 之间也不互相依赖 impl。
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        homeScreen(
            onNavigateToSettings = { navController.navigateToSettings() },
        )
        settingsScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
