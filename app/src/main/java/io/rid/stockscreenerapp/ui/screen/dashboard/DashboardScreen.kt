package io.rid.stockscreenerapp.ui.screen.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.component.AppErrDialog
import io.rid.stockscreenerapp.ui.component.AppLoadImage
import io.rid.stockscreenerapp.ui.component.AppSnackBar
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.stock.StockMarketScreen
import io.rid.stockscreenerapp.ui.screen.watchlist.WatchlistScreen
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.StockScreenerAppTheme
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import io.rid.stockscreenerapp.ui.theme.green01100
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(dashboardViewModel: DashboardViewModel = hiltViewModel(), onStockSelected: (Stock) -> Unit) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val tabs = remember { DashboardTabs.entries.toList() }
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    // Clear focus when page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { focusManager.clearFocus(force = true) }
    }

    // Show SnackBar when a stock is starred/unstarred
    DashboardSnackBar(
        snackBarHostState = snackBarHostState,
        lastStarredAction = uiState.lastStarredAction,
        onClearAction = { dashboardViewModel.clearLastStarredAction() }
    )

    uiState.err?.let {
        AppErrDialog(context = context, err = it)
    }

    Scaffold(snackbarHost = { AppSnackBar(snackBarHostState) }) {
        DashboardContent(
            uiState = uiState,
            pagerState = pagerState,
            tabs = tabs,
            onTabSelected = { index -> dashboardViewModel.onTabSelected(index, pagerState, coroutineScope) },
            onSearch = dashboardViewModel::filterStocks,
            onRefresh = dashboardViewModel::fetchStocks,
            onStockSelected = onStockSelected,
            onStockStarred = { stock -> dashboardViewModel.updateStockStar(stock) }
        )
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    pagerState: PagerState,
    tabs: List<DashboardTabs>,
    onTabSelected: (Int) -> Unit,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
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
            onStockSelected = onStockSelected,
            onStockStarred = onStockStarred
        )
    }
}

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
                MaterialTheme.typography.titleMedium.copy(color = green01100)
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
                        imageResId = if (isSelected) tab.iconHighlightResId else tab.iconResId,
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
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    val starredStock by remember(uiState.stocks) {
        derivedStateOf { uiState.stocks.filter { it.isStarred } }
    }

    val modifier = Modifier
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
            DashboardTabs.STOCK_MARKET -> StockMarketScreen(
                uiState = uiState,
                pagerState = pagerState,
                modifier = modifier,
                onRefresh = onRefresh,
                onSearch = onSearch,
                onStockSelected = onStockSelected,
                onStockStarred = onStockStarred
            )

            DashboardTabs.WATCHLIST -> WatchlistScreen(
                stocks = starredStock,
                modifier = modifier,
                onStockSelected = onStockSelected,
                onStockStarred = onStockStarred
            )
        }
    }
}

@Composable
private fun DashboardSnackBar(
    snackBarHostState: SnackbarHostState,
    lastStarredAction: StarredAction?,
    onClearAction: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(lastStarredAction) {
        lastStarredAction?.let { action ->
            val message = when (action) {
                StarredAction.STARRED -> R.string.snackbar_add_msg
                StarredAction.UNSTARRED -> R.string.snackbar_remove_msg
            }
            snackBarHostState.showSnackbar(context.getString(message))
            onClearAction()
        }
    }
}

// region Preview
// =============================================================================================================

@Preview
@Composable
private fun PreviewDashboardContent() {
    val stocks = listOf(Stock("MB", "Marrybrown"), Stock("KFC", "Kentucky Fried Chicken", true))
    val uiState = DashboardUiState(stocks = stocks)
    val tabs = DashboardTabs.entries.toList()
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    StockScreenerAppTheme {
        DashboardContent(
            uiState = uiState,
            pagerState = pagerState,
            tabs = tabs,
            onTabSelected = { },
            onSearch = { },
            onRefresh = { },
            onStockSelected = { },
            onStockStarred = { }
        )
    }
}

@Preview
@Composable
private fun PreviewDashboardTabs() {
    val tabs = DashboardTabs.entries.toList()

    StockScreenerAppTheme {
        DashboardTabs(
            tabs = tabs,
            selectedIndex = 0,
            onTabSelected = { }
        )
    }
}

@Preview
@Composable
private fun PreviewDashboardPager() {
    val stocks = listOf(Stock("MB", "Marrybrown"), Stock("KFC", "Kentucky Fried Chicken", true))
    val uiState = DashboardUiState(stocks = stocks)
    val tabs = DashboardTabs.entries.toList()
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }

    StockScreenerAppTheme {
        DashboardPager(
            uiState = uiState,
            pagerState = pagerState,
            tabs = tabs,
            onSearch = { },
            onRefresh = { },
            onStockSelected = { },
            onStockStarred = { }
        )
    }
}

// endregion ===================================================================================================