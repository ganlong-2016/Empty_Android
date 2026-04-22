package com.scania.android.feature.launcher.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scania.android.feature.launcher.api.LauncherRoute
import com.scania.android.feature.launcher.impl.LauncherScreen

/**
 * Launcher 向导航图注册自己的 composable。
 *
 * Launcher 就是车机的首屏，app 一般会把 [LauncherRoute] 作为 `NavHost.startDestination`。
 */
fun NavGraphBuilder.launcherScreen() {
    composable<LauncherRoute> {
        LauncherScreen()
    }
}
