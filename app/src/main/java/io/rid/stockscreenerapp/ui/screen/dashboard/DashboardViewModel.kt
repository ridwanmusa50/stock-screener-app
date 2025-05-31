package io.rid.stockscreenerapp.ui.screen.dashboard

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.api.interceptor.Repository
import io.rid.stockscreenerapp.data.ListingStock
import javax.inject.Inject

@Immutable
enum class DashboardTabs(val titleResId: Int, val iconResId: Int) {
    STOCK_MARKET(R.string.general_stocks, R.drawable.ic_stock_market),
    WATCHLIST(R.string.general_watchlist, R.drawable.ic_watchlist)
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    private val application: Application
) : ViewModel() {

    suspend fun getStocks() {
        repository.getStocks(application)
    }

    fun parseCsvToList(csvContent: String): List<ListingStock> {
        return csvContent
            .lineSequence()
            .drop(1) // Skip header
            .filter { it.isNotBlank() }
            .map { line ->
                val tokens = line.split(",")
                ListingStock(
                    symbol = tokens.getOrNull(0) ?: "",
                    name = tokens.getOrNull(1) ?: "",
                    exchange = tokens.getOrNull(2) ?: "",
                    assetType = tokens.getOrNull(3) ?: "",
                    ipoDate = tokens.getOrNull(4),
                    delistingDate = tokens.getOrNull(5),
                    status = tokens.getOrNull(6) ?: ""
                )
            }.toList()
    }

}
