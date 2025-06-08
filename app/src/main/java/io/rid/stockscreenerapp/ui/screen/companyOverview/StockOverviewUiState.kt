package io.rid.stockscreenerapp.ui.screen.companyOverview

import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.data.MonthlyStock
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.screen.dashboard.StarredAction

data class StockOverviewUiState(
    val isLoading: Boolean = false,
    val stock: Stock? = null,
    val companyOverview: CompanyOverview? = null,
    val monthlyStock: MonthlyStock? = null,
    val currentPrice: String? = null,
    val chartData: ChartData? = null,
    val err: ApiResponse.Err? = null,
    val lastStarredAction: StarredAction? = null
)