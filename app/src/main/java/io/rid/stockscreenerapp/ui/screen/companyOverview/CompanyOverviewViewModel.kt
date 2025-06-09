package io.rid.stockscreenerapp.ui.screen.companyOverview

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.data.MonthlyStock
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.data.TimeSeriesData
import io.rid.stockscreenerapp.database.Dao
import io.rid.stockscreenerapp.ui.screen.dashboard.StarredAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CompanyOverviewViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: Dao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockOverviewUiState())
    val uiState: StateFlow<StockOverviewUiState> = _uiState.asStateFlow()

    @SuppressLint("DefaultLocale")
    fun initialize(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val companyOverviewDeferred = async { getCompanyOverview(symbol) }
            val monthlyStockDeferred = async { getMonthlyStock(symbol) }

            val companyOverviewResult = companyOverviewDeferred.await()
            val monthlyStockResult = monthlyStockDeferred.await()

            val monthlyStockData = (monthlyStockResult as? ApiResponse.Success)?.data
            val monthlyTimeSeries = monthlyStockData?.monthlyTimeSeries

            val processedChartData = monthlyTimeSeries
                ?.takeIf { it.isNotEmpty() }
                ?.let { processChartData(it) }

            val currentPrice = monthlyTimeSeries
                ?.maxByOrNull { LocalDate.parse(it.key) }
                ?.value?.close
                ?.toDoubleOrNull()
                ?.let { "$${String.format("%.2f", it)}" }

            // Update UI state with both results
            _uiState.update {
                it.copy(
                    companyOverview = (companyOverviewResult as? ApiResponse.Success)?.data,
                    monthlyStock = (monthlyStockResult as? ApiResponse.Success)?.data,
                    chartData = processedChartData,
                    currentPrice = currentPrice,
                    err = (companyOverviewResult as? ApiResponse.Err)
                        ?: (monthlyStockResult as? ApiResponse.Err),
                    isLoading = false
                )
            }
        }
    }

    private fun processChartData(timeSeries: Map<String, TimeSeriesData>?): ChartData? {
        if (timeSeries.isNullOrEmpty()) return null

        val sortedEntries = timeSeries.toList()
            .sortedBy { LocalDate.parse(it.first) }
            .takeLast(12)

        // Create list of all entries with null handling
        val allEntries = sortedEntries.mapIndexed { index, entry ->
            ChartEntry(
                index = index,
                date = entry.first,
                price = entry.second.close.toFloatOrNull()
            )
        }

        // Filter valid entries
        val validEntries = allEntries.filter { it.price != null }

        if (validEntries.isEmpty()) return null

        // Calculate chart bounds with some padding
        val maxPrice = validEntries.maxOf { it.price!! }
        val minPrice = validEntries.minOf { it.price!! }
        val padding = (maxPrice - minPrice) * 0.1f // 10% padding

        return ChartData(
            allEntries = allEntries,
            validEntries = validEntries,
            upperBound = maxPrice + padding,
            lowerBound = (minPrice - padding).coerceAtLeast(0f),
            dates = sortedEntries.map { it.first }
        )
    }

    fun updateStockStar(stock: Stock) {
        val newStarredState = !stock.isStarred

        viewModelScope.launch(Dispatchers.IO) {
            var localStock = dao.getStocks().first { it.symbol == stock.symbol }.apply {
                isStarred = newStarredState
            }
            dao.updateStarredStock(stock.symbol, stock.currentPrice, stock.percentageChanges, newStarredState)

            _uiState.update { state ->
                state.copy(
                    stock = localStock,
                    lastStarredAction = if (newStarredState) StarredAction.STARRED else StarredAction.UNSTARRED
                )
            }
        }
    }

    fun updateStock(symbol: String, name: String, isStarred: Boolean) {
        val stock = Stock(symbol = symbol, name = name, isStarred = isStarred)
        _uiState.update { it.copy(stock = stock) }
    }

    fun clearLastStarredAction() {
        _uiState.update { it.copy(lastStarredAction = null) }
    }

    private suspend fun getCompanyOverview(symbol: String): ApiResponse<CompanyOverview> {
        return repository.getCompanyOverview(symbol)
    }

    private suspend fun getMonthlyStock(symbol: String): ApiResponse<MonthlyStock> {
        return repository.getMonthlyStock(symbol)
    }

}