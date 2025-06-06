package io.rid.stockscreenerapp.ui.screen.dashboard

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.data.ListingStock
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.database.Dao
import io.rid.stockscreenerapp.ui.util.parseStockCsv
import io.rid.stockscreenerapp.ui.util.readCsvFromRaw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
enum class DashboardTabs(@StringRes val titleResId: Int, @DrawableRes val iconResId: Int) {
    STOCK_MARKET(R.string.general_stocks, R.drawable.ic_stock_market),
    WATCHLIST(R.string.general_watchlist, R.drawable.ic_watchlist)
}

// Track the last starred action
sealed class StarredAction {
    object STARRED : StarredAction()
    object UNSTARRED : StarredAction()
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

                    when {
                        parsed.isNotEmpty() -> {
                            // Map parsed stocks with isStarred from local DB
                            val mergedStocks = mergeWithLocal(parsed, stocksFromDb)
                            dao.insertStocks(mergedStocks)
                            mergedStocks
                        }

                        stocksFromDb.isNotEmpty() -> {
                            stocksFromDb
                        }

                        else -> {
                            val fallbackContent = readCsvFromRaw(context, R.raw.listing_status)
                            val fallbackStocks = parseStockCsv(fallbackContent)
                            val mergedStocks = mergeWithLocal(fallbackStocks, stocksFromDb)

                            dao.insertStocks(mergedStocks)
                            mergedStocks
                        }
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
                    val stocks = if (filteredStock != null && filteredStock.isNotEmpty()) {
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

    fun updateStockStar(stock: Stock) {
        val newStarredState = !stock.isStarred

        viewModelScope.launch(Dispatchers.IO) {
            dao.updateStarredStock(stock.symbol, newStarredState)

            val updatedList = localStocks.value.map {
                if (it.symbol == stock.symbol) it.copy(isStarred = newStarredState) else it
            }

            localStocks.value = updatedList

            _uiState.update { state ->
                state.copy(
                    stocks = updatedList,
                    lastStarredAction = if (newStarredState) StarredAction.STARRED else StarredAction.UNSTARRED
                )
            }
        }
    }

    fun onTabSelected(index: Int, pagerState: PagerState, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    fun clearLastStarredAction() {
        _uiState.update { it.copy(lastStarredAction = null) }
    }

    private suspend fun loadStockFromRoomDb(): List<Stock>? {
        return dao.getStocks()
    }

    private fun filterFromListingStock(query: String): List<Stock> {
        return if (query.isEmpty()) {
            localStocks.value
        } else {
            localStocks.value.filter {
                it.symbol.contains(query, ignoreCase = true)
            }.sortedBy { it.symbol }
        }
    }

    private fun mergeWithLocal(listingStocks: List<ListingStock>, stock: List<Stock>): List<Stock> {
        return listingStocks.map { listingStock ->
            val localStock = stock.firstOrNull { it.symbol == listingStock.symbol }
            Stock(
                symbol = listingStock.symbol,
                name = listingStock.name,
                isStarred = localStock?.isStarred == true
            )
        }.sortedBy { it.symbol }
    }


}