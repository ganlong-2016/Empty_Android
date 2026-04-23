package com.empty.android.feature.landing.api

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data object LandingRoute

fun NavController.navigateToLanding(navOptions: NavOptions? = null) =
    navigate(route = LandingRoute, navOptions = navOptions)
