package io.rid.stockscreenerapp.ui.screen.dashboard

import android.content.Context
import androidx.compose.foundation.pager.PagerState
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

    private val localStocks = MutableStateFlow<List<Stock>>(emptyList())

    fun fetchStocks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, err = null) }

            val result = repository.getStocks()
            val stocks = when (result) {
                is ApiResponse.Success -> {
                    val csvContent = result.data.string()
                    val parsed = parseStockCsv(csvContent)
                    val stocksFromDb = loadStockFromRoomDb().orEmpty() // avoid null

                    if (parsed.isNotEmpty()) {
                        // Map parsed stocks with isStarred from local DB
                        val mergedStocks = parsed.map { stock ->
                            val localStock = stocksFromDb.firstOrNull { it.symbol == stock.symbol }
                            Stock(
                                symbol = stock.symbol,
                                name = stock.name,
                                isStarred = localStock?.isStarred == true // default false if null
                            )
                        }.sortedBy { it.symbol }

                        dao.insertStocks(mergedStocks)
                        mergedStocks
                    } else {
                        stocksFromDb
                    }
                }

                is ApiResponse.Err -> loadStockFromRoomDb()
            }

            if (stocks != null) localStocks.value = stocks

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
                    val filteredStock = result.data.filteredResults
                    val stocks = if (filteredStock?.isNotEmpty() == true) {
                        filteredStock.map { remoteStock ->
                            val localStock = localStocks.value.firstOrNull { it.symbol == remoteStock.symbol }
                            Stock(remoteStock.symbol, remoteStock.name, localStock?.isStarred == true)
                        }.sortedBy { it.symbol }
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

    fun updateStockStar(symbol: String, isStarred: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateStarredStock(symbol, !isStarred)

            // Update just one item in local cache
            val updatedList = localStocks.value.map {
                if (it.symbol == symbol) it.copy(isStarred = !isStarred) else it
            }

            localStocks.value = updatedList

            if (updatedList != _uiState.value.stocks) {
                _uiState.update { it.copy(stocks = updatedList) }
            }
        }
    }

    fun onTabSelected(index: Int, pagerState: PagerState) {
        viewModelScope.launch {
            pagerState.animateScrollToPage(index)
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