package io.rid.stockscreenerapp.ui.screen.dashboard

import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.data.Stock

data class DashboardUiState(
    val isLoading: Boolean = false,
    val originalStocks: List<Stock> = emptyList(),
    val filteredStocks: List<Stock> = emptyList(),
    val err: ApiResponse.Err? = null,
    val lastStarredAction: StarredAction? = null
)