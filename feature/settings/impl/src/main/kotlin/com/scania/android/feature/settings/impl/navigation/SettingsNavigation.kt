package com.scania.android.feature.settings.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scania.android.feature.settings.api.SettingsRoute
import com.scania.android.feature.settings.impl.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(onBack = onBack)
    }
}
