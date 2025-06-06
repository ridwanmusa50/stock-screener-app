package io.rid.stockscreenerapp.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.rid.stockscreenerapp.ui.component.AppLoadImage
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.stock.StockMarketScreen
import io.rid.stockscreenerapp.ui.screen.watchlist.WatchlistScreen
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun DashboardScreen(dashboardViewModel: DashboardViewModel = hiltViewModel()) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val tabs = remember { DashboardTabs.entries.toList() }
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    DashboardContent(
        uiState = uiState,
        pagerState = pagerState,
        tabs = tabs,
        onTabSelected = { index -> dashboardViewModel.onTabSelected(index, pagerState) },
        onSearch = dashboardViewModel::filterStocks,
        onRefresh = dashboardViewModel::fetchStocks,
        onStockStarred = { symbol, isStarred ->
            dashboardViewModel.updateStockStar(symbol, isStarred)
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    pagerState: PagerState,
    tabs: List<DashboardTabs>,
    onTabSelected: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onStockStarred: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary)
            .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility)
            .navigationBarsPadding(),
    ) {
        DashboardTabs(
            tabs = tabs,
            selectedIndex = pagerState.currentPage,
            onTabSelected = onTabSelected
        )
        DashboardPager(
            tabs = tabs,
            pagerState = pagerState,
            uiState = uiState,
            onSearch = onSearch,
            onRefresh = onRefresh,
            onStockStarred = onStockStarred
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTabs(
    tabs: List<DashboardTabs>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        divider = { },
        indicator = { }
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex

            Tab(
                selected = isSelected,
                text = {
                    AppTxt(
                        txtResId = tab.titleResId,
                        style = if (isSelected) MaterialTheme.typography.titleMedium.copy(color = Color.White)
                                else MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    )
                },
                icon = {
                    AppLoadImage(
                        imageResId = tab.iconResId,
                        modifier = Modifier.size(
                            if (isSelected) Dimen.Size.tabIconExpanded
                            else Dimen.Size.tabIconDefault
                        ),
                    )
                },
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun DashboardPager(
    tabs: List<DashboardTabs>,
    pagerState: PagerState,
    uiState: DashboardUiState,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onStockStarred: (String, Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val starredStock by remember(uiState.stocks) {
        derivedStateOf {
            uiState.stocks.filter { it.isStarred }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        pageSpacing = Spacing.spacing4,
        verticalAlignment = Alignment.Top
    ) { page ->
        val screenModifier = Modifier
            .fillMaxSize()
            .background(color = Color.White, shape = fullRoundedCornerShape.extraLarge)
            .padding(top = Spacing.spacing16)

        when (tabs[page]) {
            DashboardTabs.STOCK_MARKET -> {
                StockMarketScreen(
                    uiState = uiState,
                    pagerState = pagerState,
                    modifier = screenModifier,
                    onRefresh = onRefresh,
                    onSearch = onSearch,
                    onStockSelected = { },
                    onStockStarred = onStockStarred
                )
            }

            DashboardTabs.WATCHLIST -> {
                WatchlistScreen(
                    stocks = starredStock,
                    modifier = screenModifier,
                    onStockSelected = { },
                    onStockStarred = onStockStarred
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        focusManager.clearFocus(force = true)
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { !it }
            .first()

        if (pagerState.currentPage == DashboardTabs.STOCK_MARKET.ordinal) {
            onRefresh()
        }
    }
}