package com.empty.android.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.empty.android.feature.home.navigation.HomeRoute
import com.empty.android.feature.home.navigation.homeScreen
import com.empty.android.feature.settings.navigation.navigateToSettings
import com.empty.android.feature.settings.navigation.settingsScreen

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
