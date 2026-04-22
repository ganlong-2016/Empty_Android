package com.scania.android.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.scania.android.feature.home.impl.navigation.homeScreen
import com.scania.android.feature.launcher.api.LauncherRoute
import com.scania.android.feature.launcher.impl.navigation.launcherScreen
import com.scania.android.feature.settings.api.navigateToSettings
import com.scania.android.feature.settings.impl.navigation.settingsScreen

/**
 * App 级导航图：
 * - 起点是 Launcher（车机开机直接到桌面）；
 * - Home、Settings 作为桌面上可跳转的二级页面保留。
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LauncherRoute) {
        launcherScreen()
        homeScreen(
            onNavigateToSettings = { navController.navigateToSettings() },
        )
        settingsScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
