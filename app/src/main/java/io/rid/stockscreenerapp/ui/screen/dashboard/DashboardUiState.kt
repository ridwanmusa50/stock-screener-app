package io.rid.stockscreenerapp.ui.screen.dashboard

import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.data.ListingStock

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stocks: List<ListingStock> = emptyList(),
    val err: ApiResponse.Err? = null
)