package com.empty.android.feature.landing.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.empty.android.feature.landing.api.LandingRoute
import com.empty.android.feature.landing.impl.LandingScreen

fun NavGraphBuilder.landingScreen(
    onNavigateToHome: () -> Unit,
) {
    composable<LandingRoute> {
        LandingScreen(onNavigateToHome = onNavigateToHome)
    }
}
