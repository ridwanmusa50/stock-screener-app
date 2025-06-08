package io.rid.stockscreenerapp.ui.screen.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.component.AppImageBtn
import io.rid.stockscreenerapp.ui.component.AppOutlinedTxtField
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.dashboard.DashboardTabs
import io.rid.stockscreenerapp.ui.screen.dashboard.DashboardUiState
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import io.rid.stockscreenerapp.ui.util.Const.Common
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun StockMarketScreen(
    uiState: DashboardUiState,
    pagerState: PagerState,
    modifier: Modifier,
    onRefresh: () -> Unit,
    onSearch: (String) -> Unit,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }

    // Reset search on tab switch
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(Common.SEARCH_DEBOUNCE)
            .distinctUntilChanged()
            .collectLatest { onSearch(it) }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == DashboardTabs.WATCHLIST.ordinal) {
            query = ""
        }
    }

    Column(modifier = modifier.navigationBarsPadding()) {
        SearchBar(query = query, onQueryChanged = { query = it })

        StockList(
            uiState = uiState,
            onRefresh = onRefresh,
            onStockSelected = onStockSelected,
            onStockStarred = onStockStarred
        )
    }
}

@Composable
private fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    val focusManager = LocalFocusManager.current

    AppOutlinedTxtField(
        value = query,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacing4),
        focusedContainerColor = Color.Transparent,
        unFocusedContainerColor = Color.Transparent,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unFocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
        leadingIconResId = R.drawable.ic_search,
        placeHolderTxtResId = R.string.placeholder_search_stock,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus(true) }),
        onValueChange = onQueryChanged
    )
}

@Composable
private fun StockList(
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = rememberPullToRefreshState(),
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh
    ) {
        LazyColumn {
            items(count = uiState.stocks.size) { index ->
                val stock = uiState.stocks[index]
                StockMarketItem(stock = stock, onStockSelected = onStockSelected, onStockStarred = onStockStarred)
            }
        }
    }
}

@Composable
private fun StockMarketItem(
    stock: Stock,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = MaterialTheme.colorScheme.primary),
                onClick = { onStockSelected(stock) }
            )
    ) {
        val (txtSymbol, txtName, btnFavourite) = createRefs()

        AppTxt(
            txt = stock.symbol,
            modifier = Modifier
                .constrainAs(txtSymbol) {
                    start.linkTo(parent.start)
                    end.linkTo(btnFavourite.start)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .padding(start = Spacing.spacing8, end = Spacing.spacing4, top = Spacing.spacing8),
            style = MaterialTheme.typography.titleMedium,
        )

        AppTxt(
            txt = stock.name,
            modifier = Modifier
                .constrainAs(txtName) {
                    start.linkTo(parent.start)
                    end.linkTo(btnFavourite.start)
                    top.linkTo(txtSymbol.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(
                    start = Spacing.spacing8,
                    end = Spacing.spacing4,
                    bottom = Spacing.spacing8
                ),
            style = MaterialTheme.typography.bodyMedium,
        )

        AppImageBtn(
            imageResId = if (stock.isStarred) R.drawable.ic_starred else R.drawable.ic_unstar,
            backgroundModifier = Modifier
                .constrainAs(btnFavourite) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            imageModifier = Modifier.size(Dimen.Size.icStar),
            contentScale = ContentScale.Fit,
            onClick = { onStockStarred(stock) }
        )
    }
}

// region Preview
// =============================================================================================================

@Preview
@Composable
private fun PreviewStockMarketScreen() {
    val stocks = listOf(Stock("MB", "Marrybrown"), Stock("KFC", "Kentucky Fried Chicken", true))
    val uiState = DashboardUiState(stocks = stocks)
    val tabs = DashboardTabs.entries.toList()
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White, shape = fullRoundedCornerShape.extraLarge)
        .padding(top = Spacing.spacing16)

    StockMarketScreen(
        uiState = uiState,
        pagerState = pagerState,
        modifier = modifier,
        onRefresh = { },
        onSearch = { },
        onStockSelected = { },
        onStockStarred = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchBarDefault() {
    SearchBar(query = "", onQueryChanged = { })
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchBarFilled() {
    SearchBar(query = "KFC", onQueryChanged = { })
}

@Preview(showBackground = true)
@Composable
private fun PreviewStockListLoading() {
    val stocks = listOf(Stock("MB", "Marrybrown"), Stock("KFC", "Kentucky Fried Chicken", true))
    val uiState = DashboardUiState(stocks = stocks)

    StockList(
        uiState = uiState,
        onRefresh = { },
        onStockSelected = { },
        onStockStarred = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewStockMarketItemDefault() {
    val stock = Stock("MB", "Marrybrown")
    StockMarketItem(stock = stock, onStockSelected = { }, onStockStarred = { })
}

@Preview(showBackground = true)
@Composable
private fun PreviewStockMarketItemStarred() {
    val stock = Stock("KFC", "Kentucky Fried Chicken", true)
    StockMarketItem(stock = stock, onStockSelected = { }, onStockStarred = { })
}

// endregion ===================================================================================================