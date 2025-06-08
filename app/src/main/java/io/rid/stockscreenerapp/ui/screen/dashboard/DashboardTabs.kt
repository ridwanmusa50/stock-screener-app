package io.rid.stockscreenerapp.ui.screen.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import io.rid.stockscreenerapp.R

@Stable
enum class DashboardTabs(
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int,
    @DrawableRes val iconHighlightResId: Int
) {
    STOCK_MARKET(R.string.general_stocks, R.drawable.ic_stock_market, R.drawable.ic_stock_market_highlight),
    WATCHLIST(R.string.general_watchlist, R.drawable.ic_watchlist, R.drawable.ic_watchlist_highlight)
}