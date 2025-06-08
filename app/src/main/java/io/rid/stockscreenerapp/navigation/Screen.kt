package io.rid.stockscreenerapp.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable data object Splash : Screen()

    @Serializable data object Dashboard : Screen()

    @Serializable data class StockOverview(val symbol: String, val name: String, val isStarred: Boolean) : Screen()

}