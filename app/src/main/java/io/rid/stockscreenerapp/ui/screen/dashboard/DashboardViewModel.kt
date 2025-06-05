package io.rid.stockscreenerapp.ui.screen.dashboard

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.database.Dao
import io.rid.stockscreenerapp.ui.util.parseStockCsv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
enum class DashboardTabs(val titleResId: Int, val iconResId: Int) {
    STOCK_MARKET(R.string.general_stocks, R.drawable.ic_stock_market),
    WATCHLIST(R.string.general_watchlist, R.drawable.ic_watchlist)
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
    private val dao: Dao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun fetchStocks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, err = null) }

            val result = repository.getStocks()
            val stocks = when (result) {
                is ApiResponse.Success -> {
                    val csvContent = result.data.string()
                    val parsed = parseStockCsv(csvContent)

                    if (parsed.isNotEmpty()) {
                        val stocks = parsed.map { stock ->
                            Stock(stock.symbol, stock.name)
                        }.sortedBy { stock -> stock.symbol }

                        dao.insertStocks(stocks)
                        stocks
                    } else {
                        loadStockFromRoomDb()
                    }
                }

                is ApiResponse.Err -> loadStockFromRoomDb()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    stocks = stocks ?: emptyList(),
                    err = if (stocks == null) result as? ApiResponse.Err else null
                )
            }
        }
    }

    fun filterStocks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.filterStocks(query)) {
                is ApiResponse.Success -> {
                    val stocks = if (result.data.filteredResults.isNotEmpty()) {
                        result.data.filteredResults
                            .map { Stock(it.symbol, it.name) }
                            .sortedBy { it.symbol }
                    } else {
                        filterFromListingStock(query)
                    }
                    _uiState.update { it.copy(stocks = stocks) }
                }

                is ApiResponse.Err -> {
                    _uiState.update { it.copy(err = result) }
                }
            }
        }
    }

    private suspend fun loadStockFromRoomDb(): List<Stock>? {
        return dao.getStocks()
    }

    private fun filterFromListingStock(query: String): List<Stock> {
        return _uiState.value.stocks.filter {
            it.symbol.contains(query, ignoreCase = true)
        }.sortedBy { it.symbol }
    }

}