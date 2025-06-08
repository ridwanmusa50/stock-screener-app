package io.rid.stockscreenerapp.ui.screen.companyOverview

data class ChartData(
    val allEntries: List<ChartEntry>,
    val validEntries: List<ChartEntry>,
    val upperBound: Float,
    val lowerBound: Float,
    val dates: List<String>
)