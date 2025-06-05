package io.rid.stockscreenerapp.ui.screen.stock

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.ListingStock
import io.rid.stockscreenerapp.ui.component.AppImageBtn
import io.rid.stockscreenerapp.ui.component.AppOutlinedTxtField
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.dashboard.DashboardUiState
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import kotlinx.coroutines.FlowPreview

@Composable
fun StockMarketScreen(
    uiState: DashboardUiState,
    stocks: List<ListingStock>,
    modifier: Modifier,
    pagerState: PagerState,
    onRefresh: () -> Unit,
    onSearch: (String) -> Unit
) {
    val currentRefresh by rememberUpdatedState(onRefresh)
    val currentSearch by rememberUpdatedState(onSearch)

    LaunchedEffect(Unit) {
        if (uiState.stocks.isEmpty()) currentRefresh()
    }

    StockMarketContent(
        uiState = uiState,
        stocks = stocks,
        modifier = modifier,
        pagerState = pagerState,
        onRefresh = currentRefresh,
        onSearchResult = currentSearch
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun StockMarketContent(
    uiState: DashboardUiState,
    stocks: List<ListingStock>,
    modifier: Modifier,
    pagerState: PagerState,
    onRefresh: () -> Unit,
    onSearchResult: (String) -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.navigationBarsPadding()) {
        SearchBar(
            search = search,
            onSearchResult = {
                search = it
                onSearchResult.invoke(it)
            }
        )

        StockList(uiState = uiState, stocks = stocks, onRefresh = onRefresh, onStockSelected = {
            // TODO navigate to next screen
        })
    }

    LaunchedEffect(pagerState.currentPage) {
        search = ""
    }
}

@Composable
private fun SearchBar(search: String, onSearchResult: (String) -> Unit) {
    val focusManager = LocalFocusManager.current

    AppOutlinedTxtField(
        value = search,
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
        onValueChange = onSearchResult
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockList(
    uiState: DashboardUiState,
    stocks: List<ListingStock>,
    onRefresh: () -> Unit,
    onStockSelected: (ListingStock) -> Unit
) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = rememberPullToRefreshState(),
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh
    ) {
        LazyColumn {
            items(count = stocks.size) { index ->
                val stock = stocks[index]
                StockMarket(stock = stock, onStockSelected = onStockSelected)
            }
        }
    }
}

@Composable
private fun StockMarket(stock: ListingStock, onStockSelected: (ListingStock) -> Unit) {
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
                .padding(start = Spacing.spacing8, end = Spacing.spacing4, bottom = Spacing.spacing8),
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
            contentScale = ContentScale.Fit
        ) {
            // TODO update starred to local db
        }
    }
}