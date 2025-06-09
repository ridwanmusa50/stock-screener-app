package io.rid.stockscreenerapp.ui.screen.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import io.rid.stockscreenerapp.dataStore.PrefsStore
import io.rid.stockscreenerapp.dataStore.PrefsStore.Companion.LAST_STARRED_STOCK_MONTHLY_STOCK_LOADED
import io.rid.stockscreenerapp.database.Dao
import io.rid.stockscreenerapp.ui.util.Utils.parseStockCsv
import io.rid.stockscreenerapp.ui.util.Utils.readCsvFromRaw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
    private val dao: Dao,
    private val prefsStore: PrefsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val lastStarredStockMonthlyStockLoaded: StateFlow<Long?> = prefsStore.lastStarredStockMonthlyStockLoaded
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
                                symbol = remoteFilteredStock.symbol,
                                name = remoteFilteredStock.name,
                                isStarred = localStock?.isStarred == true
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
            dao.updateStarredStock(stock.symbol, stock.currentPrice, stock.percentageChanges, newStarredState)

            val updatedList = _uiState.value.originalStocks.map {
                if (it.symbol == stock.symbol) it.copy(isStarred = newStarredState) else it
            }

            _uiState.update { state ->
                state.copy(
                    originalStocks = updatedList,
                    filteredStocks = updatedList,
                    lastStarredAction = if (newStarredState) StarredAction.STARRED else StarredAction.UNSTARRED
                )
            }
        }
    }

    fun onTabSelected(index: Int, pagerState: PagerState, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = false, err = null) }
            pagerState.animateScrollToPage(index)
        }
    }

    fun clearLastStarredAction() {
        _uiState.update { it.copy(lastStarredAction = null) }
    }

    fun getMonthlyStock(stocks: List<Stock>) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastLoadedTime = lastStarredStockMonthlyStockLoaded.value
            val isOutdated = lastLoadedTime?.let {
                val now = System.currentTimeMillis()
                now - it > 24 * 60 * 60 * 1000 // 24 hours in milliseconds
            } ?: true // If null, consider outdated

            var fetchedAny = false

            stocks.forEach { stock ->
                val needsFetching = stock.currentPrice == null || stock.percentageChanges == null || isOutdated

                if (needsFetching) {
                    _uiState.update { it.copy(isLoading = true, err = null) }
                    getMonthlyStock(this, stock)
                    fetchedAny = true
                }
            }

            // Update the timestamp only if at least one API call was made
            if (fetchedAny) {
                prefsStore.writeSingleDataToDataStore(
                    LAST_STARRED_STOCK_MONTHLY_STOCK_LOADED,
                    System.currentTimeMillis()
                )
                _uiState.update { it.copy(isLoading = false, err = null) }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getMonthlyStock(coroutineScope: CoroutineScope, stock: Stock) {
        coroutineScope.launch {
            when (val result = repository.getMonthlyStock(stock.symbol)) {
                is ApiResponse.Success -> {
                    val monthlyTimeSeries = result.data.monthlyTimeSeries

                    if (!monthlyTimeSeries.isNullOrEmpty()) {
                        val sortedDates = monthlyTimeSeries.keys
                            .sortedByDescending { LocalDate.parse(it) }

                        if (sortedDates.size >= 2) {
                            val currentClose = monthlyTimeSeries[sortedDates[0]]?.close?.toDoubleOrNull()
                            val lastClose = monthlyTimeSeries[sortedDates[1]]?.close?.toDoubleOrNull()

                            val currentPrice = currentClose?.let { "$${String.format("%.2f", it)}" }
                            val percentageChange = if (currentClose != null && lastClose != null && lastClose != 0.0) {
                                val change = ((currentClose - lastClose) / lastClose) * 100
                                String.format("%.2f%%", change)
                            } else null

                            // Update DB
                            dao.updateStarredStock(
                                symbol = stock.symbol,
                                currentPrice = currentPrice,
                                percentageChanges = percentageChange,
                                isStarred = stock.isStarred
                            )

                            // Update originalStocks list
                            val updatedOriginal = _uiState.value.originalStocks.map {
                                if (it.symbol == stock.symbol) {
                                    it.copy(currentPrice = currentPrice, percentageChanges = percentageChange)
                                } else it
                            }

                            // Update filteredStocks list
                            val updatedFiltered = _uiState.value.filteredStocks.map {
                                if (it.symbol == stock.symbol) {
                                    it.copy(currentPrice = currentPrice, percentageChanges = percentageChange)
                                } else it
                            }

                            _uiState.update { current ->
                                current.copy(
                                    isLoading = false,
                                    originalStocks = updatedOriginal,
                                    filteredStocks = updatedFiltered
                                )
                            }
                        }
                    } else {
                        Log.w("API Monthly", "monthlyTimeSeries is null or empty for ${stock.symbol}")
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }

                is ApiResponse.Err -> {
                    _uiState.update { it.copy(isLoading = false) }
                    Log.w("API Monthly", "Error: $result for $stock")
                }
            }
        }
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