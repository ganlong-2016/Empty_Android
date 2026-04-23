package com.empty.android.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.empty.android.feature.home.api.HomeRoute
import com.empty.android.feature.home.api.navigateToHome
import com.empty.android.feature.home.impl.navigation.homeScreen
import com.empty.android.feature.landing.api.LandingRoute
import com.empty.android.feature.landing.impl.navigation.landingScreen
import com.empty.android.feature.settings.api.navigateToSettings
import com.empty.android.feature.settings.impl.navigation.settingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LandingRoute) {
        landingScreen(
            onNavigateToHome = {
                navController.navigateToHome(
                    navOptions = androidx.navigation.navOptions {
                        popUpTo<LandingRoute> { inclusive = true }
                    },
                )
            },
        )
        homeScreen(
            onNavigateToSettings = { navController.navigateToSettings() },
        )
        settingsScreen(
            onBack = { navController.popBackStack() },
        )
    }
}
