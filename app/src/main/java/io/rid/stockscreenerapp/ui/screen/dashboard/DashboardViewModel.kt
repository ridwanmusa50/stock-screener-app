package io.rid.stockscreenerapp.ui.screen.dashboard

import android.content.Context
import androidx.compose.foundation.pager.PagerState
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
import io.rid.stockscreenerapp.ui.util.Utils.parseStockCsv
import io.rid.stockscreenerapp.ui.util.Utils.readCsvFromRaw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
    private val dao: Dao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        fetchStocks()
    }

    fun fetchStocks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, err = null) }

            when ( val result = repository.getStocks()) {
                is ApiResponse.Success -> {
                    val apiCsvContent = result.data.string()
                    val apiCsvListingStocks = parseStockCsv(apiCsvContent)

                    val stocksFromDb = loadStockFromRoomDb().orEmpty()

                    // Collect stocks from: api > room database > local csv file
                    val stocks = when {
                        apiCsvListingStocks.isNotEmpty() -> {
                            // Map parsed stocks with isStarred from local DB
                            val mergedStocks = mergeWithLocal(apiCsvListingStocks, stocksFromDb)

                            dao.insertStocks(mergedStocks)
                            mergedStocks
                        }

                        stocksFromDb.isNotEmpty() -> {
                            stocksFromDb
                        }

                        else -> {
                            val localCsvContent = readCsvFromRaw(context, R.raw.listing_status)
                            val localCsvListingStocks = parseStockCsv(localCsvContent)
                            val mergedStocks = mergeWithLocal(localCsvListingStocks, stocksFromDb)

                            dao.insertStocks(mergedStocks)
                            mergedStocks
                        }
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            originalStocks = stocks,
                            filteredStocks = stocks,
                            err = null
                        )
                    }
                }

                is ApiResponse.Err -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            originalStocks = emptyList(),
                            filteredStocks = emptyList(),
                            err = result
                        )
                    }
                }
            }
        }
    }

    fun filterStocks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _uiState.value

            // If query is empty, show all original stocks
            if (query.isEmpty()) {
                _uiState.update {
                    it.copy(filteredStocks = it.originalStocks, err = null)
                }
                return@launch
            }

            // First try to filter from remote
            when (val result = repository.filterStocks(query)) {
                is ApiResponse.Success -> {
                    val remoteFilteredStocks = result.data.filteredResults

                    val filteredStocks = if (remoteFilteredStocks != null && remoteFilteredStocks.isNotEmpty()) {
                        remoteFilteredStocks.map { remoteFilteredStock ->
                            val localStock = currentState.originalStocks.firstOrNull {
                                it.symbol == remoteFilteredStock.symbol
                            }
                            Stock(
                                remoteFilteredStock.symbol,
                                remoteFilteredStock.name,
                                localStock?.isStarred == true
                            )
                        }.sortedBy { it.symbol }
                    } else {
                        // Fallback to local filtering if remote fails or returns empty
                        currentState.originalStocks.filter {
                            it.symbol.contains(query, ignoreCase = true)
                        }.sortedBy { it.symbol }
                    }

                    _uiState.update {
                        it.copy(filteredStocks = filteredStocks, err = null)
                    }
                }

                is ApiResponse.Err -> {
                    // Fallback to local filtering if remote fails
                    val filteredStocks = currentState.originalStocks.filter {
                        it.symbol.contains(query, ignoreCase = true) ||
                                it.name.contains(query, ignoreCase = true)
                    }.sortedBy { it.symbol }

                    _uiState.update {
                        it.copy(
                            filteredStocks = filteredStocks,
                            err = result
                        )
                    }
                }
            }
        }
    }

    fun updateStockStar(stock: Stock) {
        val newStarredState = !stock.isStarred

        viewModelScope.launch(Dispatchers.IO) {
            dao.updateStarredStock(stock.symbol, newStarredState)

            val updatedList = _uiState.value.originalStocks.map {
                if (it.symbol == stock.symbol) it.copy(isStarred = newStarredState) else it
            }

            _uiState.update { state ->
                state.copy(
                    originalStocks = updatedList,
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

    // Collect from room database
    private suspend fun loadStockFromRoomDb(): List<Stock>? {
        return dao.getStocks()
    }

    // Combine stocks with isStarred value from room database
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