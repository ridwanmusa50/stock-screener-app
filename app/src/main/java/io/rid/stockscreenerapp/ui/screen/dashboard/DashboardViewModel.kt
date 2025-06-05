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
import io.rid.stockscreenerapp.data.ListingStock
import io.rid.stockscreenerapp.data.Stocks
import io.rid.stockscreenerapp.ui.util.parseStockCsv
import io.rid.stockscreenerapp.ui.util.saveCsv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

const val FILE_NAME = "stocks.csv"

@Stable
enum class DashboardTabs(val titleResId: Int, val iconResId: Int) {
    STOCK_MARKET(R.string.general_stocks, R.drawable.ic_stock_market),
    WATCHLIST(R.string.general_watchlist, R.drawable.ic_watchlist)
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun fetchStocks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, err = null) }

            val result = repository.getStocks()
            val stocks = when (result) {
                is ApiResponse.Success -> {
                    val parsed = parseStockCsv(result.data.toString())
                    if (parsed.isNotEmpty()) {
                        context.saveCsv(result.data, context.getExternalFilesDir(null)!!, FILE_NAME)
                        parsed
                    } else {
                        loadCachedStocks()
                    }
                }
                is ApiResponse.Err -> loadCachedStocks()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    stocks = stocks?.map { s -> Stocks(s.symbol, s.name) }?.sortedBy { s -> s.symbol } ?: emptyList(),
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
                            .map { Stocks(it.symbol, it.name) }
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

    private fun loadCachedStocks(): List<ListingStock>? {
        val cachedFile = File(context.getExternalFilesDir(null), FILE_NAME)
        return if (cachedFile.exists()) {
            parseStockCsv(cachedFile.readText()).takeIf { it.isNotEmpty() }
        } else {
            null
        }
    }

    private fun filterFromListingStock(query: String): List<Stocks> {
        return _uiState.value.stocks.filter {
            it.symbol.contains(query, ignoreCase = true)
        }.sortedBy { it.symbol }
    }

}