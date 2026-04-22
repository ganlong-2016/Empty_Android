package com.empty.android.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.empty.android.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions = navOptions)

fun NavGraphBuilder.homeScreen(
    onNavigateToSettings: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(onNavigateToSettings = onNavigateToSettings)
    }
}
