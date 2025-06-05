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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen() {
    val dashboardViewModel = hiltViewModel<DashboardViewModel>()
    val pagerState = rememberPagerState(initialPage = 0) { DashboardTabs.entries.size }
    val coroutineScope = rememberCoroutineScope()
    val tabs = remember { DashboardTabs.entries.toList() }

    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        pagerState = pagerState,
        tabs = tabs,
        onTabSelected = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } },
        onSearch = dashboardViewModel::filterStocks,
        onRefresh = dashboardViewModel::fetchStocks
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
    onRefresh: () -> Unit
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
            onRefresh = onRefresh
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
    onRefresh: () -> Unit
) {
    val focusManager = LocalFocusManager.current

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
                    modifier = screenModifier,
                    onRefresh = onRefresh,
                    onSearch = onSearch
                )
            }

            DashboardTabs.WATCHLIST -> {
                WatchlistScreen(
                    uiState = uiState,
                    modifier = screenModifier
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