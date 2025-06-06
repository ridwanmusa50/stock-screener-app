package io.rid.stockscreenerapp.ui.screen.companyOverview

import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.data.MonthlyStock
import io.rid.stockscreenerapp.data.Stock

data class CompanyOverviewUiState(
    val isLoading: Boolean = false,
    val stock: Stock = Stock(),
    val companyOverview: CompanyOverview = CompanyOverview(),
    val monthlyStock: MonthlyStock = MonthlyStock(),
    val err: ApiResponse.Err? = null
)