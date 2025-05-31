package io.rid.stockscreenerapp.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable data object Splash : Screen()

    @Serializable data object Main : Screen()
    @Serializable data object Dashboard : Screen()
    @Serializable data object Stock : Screen()
    @Serializable data object Watchlist : Screen()

}