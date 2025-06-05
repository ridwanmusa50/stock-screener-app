package io.rid.stockscreenerapp.ui.screen.dashboard

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.data.ListingStock
import io.rid.stockscreenerapp.ui.util.Const
import io.rid.stockscreenerapp.ui.util.parseStockCsv
import io.rid.stockscreenerapp.ui.util.saveCsv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

const val FILE_NAME = "stocks.csv"

@Immutable
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

    val searchQuery = MutableStateFlow("")
    private val actualResults = MutableStateFlow<List<ListingStock>>(emptyList())

    // Filtered results based on search query
    @Suppress("OPT_IN_USAGE")
    val filteredResults: StateFlow<List<ListingStock>> = searchQuery
        .debounce(Const.Common.SEARCH_DEBOUNCE) // 300ms debounce window
        .combine(actualResults) { query, stocks ->
            if (query.isBlank()) {
                stocks
            } else {
                stocks.filter {
                    it.symbol.contains(query, ignoreCase = true) ||
                    it.name.contains(query, ignoreCase = true)
                }
            }.sortedBy { it.symbol }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun fetchStocks() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { it.copy(isLoading = true, err = null) }

        val result = repository.getStocks()
        val stocks = when (result) {
            is ApiResponse.Success -> {
                val file = context.saveCsv(result.data, context.getExternalFilesDir(null)!!, FILE_NAME)
                file?.bufferedReader()?.use { loadStocksFromCsv(it.readText()) }
            }
            is ApiResponse.Err -> {
                val cachedFile = File(context.getExternalFilesDir(null), FILE_NAME)
                if (cachedFile.exists()) loadStocksFromCsv(cachedFile.readText()) else null
            }
        }

        if (stocks != null) {
            actualResults.value = stocks
            _uiState.update { it.copy(isLoading = false, stocks = stocks) }
        } else {
            _uiState.update { it.copy(isLoading = false, err = result as? ApiResponse.Err) }
        }
    }

    fun filterStocks(input: String) {
        searchQuery.value = input
    }

    private fun loadStocksFromCsv(csvContent: String): List<ListingStock> {
        return parseStockCsv(csvContent)
    }

}