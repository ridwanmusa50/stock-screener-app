package io.rid.stockscreenerapp.ui.screen.dashboard

import android.annotation.SuppressLint
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.component.AppLoadImage
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.stock.StockMarketScreen
import io.rid.stockscreenerapp.ui.screen.watchlist.WatchlistScreen
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(dashboardViewModel: DashboardViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val tabs = remember { DashboardTabs.entries.toList() }
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect {
            focusManager.clearFocus(force = true)
        }
    }

    // Show SnackBar when a stock is starred/unstarred
    LaunchedEffect(uiState.lastStarredAction) {
        uiState.lastStarredAction?.let { action ->
            val msgResId = when (action) {
                StarredAction.STARRED -> R.string.snackbar_add_msg
                StarredAction.UNSTARRED -> R.string.snackbar_remove_msg
            }

            snackBarHostState.showSnackbar(context.getString(msgResId))
            dashboardViewModel.clearLastStarredAction() // Reset after showing
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(Spacing.spacing16),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                    snackbarData = data
                )
            }
        }
    ) {
        DashboardContent(
            uiState = uiState,
            pagerState = pagerState,
            tabs = tabs,
            onTabSelected = { index ->
                dashboardViewModel.onTabSelected(
                    index,
                    pagerState,
                    coroutineScope
                )
            },
            onSearch = dashboardViewModel::filterStocks,
            onRefresh = dashboardViewModel::fetchStocks,
            onStockStarred = { stock -> dashboardViewModel.updateStockStar(stock) }
        )
    }
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
    onStockStarred: (Stock) -> Unit
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
            val textStyle = if (isSelected) {
                MaterialTheme.typography.titleMedium.copy(color = Color.White)
            } else {
                MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            }

            Tab(
                selected = isSelected,
                text = {
                    AppTxt(
                        txtResId = tab.titleResId,
                        style = textStyle
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
    onStockStarred: (Stock) -> Unit,
) {
    val starredStock by remember(uiState.stocks) {
        derivedStateOf { uiState.stocks.filter { it.isStarred } }
    }
    val screenModifier = Modifier
        .fillMaxSize()
        .background(color = Color.White, shape = fullRoundedCornerShape.extraLarge)
        .padding(top = Spacing.spacing16)

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        pageSpacing = Spacing.spacing4,
        verticalAlignment = Alignment.Top
    ) { page ->
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

}