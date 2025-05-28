package io.rid.stockscreenerapp.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable data object Splash : Screen()

    @Serializable data object Dashboard : Screen()

}