package com.empty.android.feature.settings.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.empty.android.feature.settings.api.SettingsRoute
import com.empty.android.feature.settings.impl.SettingsScreen

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(onBack = onBack)
    }
}
