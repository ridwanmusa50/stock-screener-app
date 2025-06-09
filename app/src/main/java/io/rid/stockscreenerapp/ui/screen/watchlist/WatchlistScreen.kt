package io.rid.stockscreenerapp.ui.screen.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.green02100
import io.rid.stockscreenerapp.ui.theme.red01100

@Composable
fun WatchlistScreen(
    stocks: List<Stock>,
    modifier: Modifier,
    onStockSelected: (Stock) -> Unit,
    onGetMonthlyDifference: (List<Stock>) -> Unit
) {
    val starredStocks = stocks.filter { it.isStarred }

    LaunchedEffect(starredStocks) {
        if (starredStocks.isNotEmpty()) onGetMonthlyDifference(starredStocks)
    }

    LazyColumn(modifier = modifier) {
        items(count = stocks.size) { index ->
            val stock = stocks[index]
            WatchlistItem(stock = stock, onStockSelected = onStockSelected)
        }
    }
}

@Composable
private fun WatchlistItem(
    stock: Stock,
    onStockSelected: (Stock) -> Unit
) {
    val txtColor by remember(stock.percentageChanges) {
        derivedStateOf {
            val percent = stock.percentageChanges?.replace("%", "")?.toFloatOrNull()
            if (percent != null && percent > 0) green02100 else red01100
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = MaterialTheme.colorScheme.primary),
                onClick = { onStockSelected(stock) }
            )
    ) {
        val (txtSymbol, txtName, txtCurrentPrice, txtPercentageDiff) = createRefs()

        AppTxt(
            txt = stock.symbol,
            modifier = Modifier
                .constrainAs(txtSymbol) {
                    start.linkTo(parent.start)
                    end.linkTo(txtCurrentPrice.start)
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
                    end.linkTo(txtCurrentPrice.start)
                    top.linkTo(txtSymbol.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(start = Spacing.spacing8, end = Spacing.spacing4, bottom = Spacing.spacing8),
            style = MaterialTheme.typography.bodyMedium,
        )

        AppTxt(
            txt = stock.currentPrice.orEmpty(),
            modifier = Modifier
                .constrainAs(txtCurrentPrice) {
                    end.linkTo(txtPercentageDiff.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = Spacing.spacing8, end = Spacing.spacing4, bottom = Spacing.spacing8),
            style = MaterialTheme.typography.titleMedium.copy(txtColor),
        )

        AppTxt(
            txt = stock.percentageChanges.orEmpty(),
            modifier = Modifier
                .constrainAs(txtPercentageDiff) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = Spacing.spacing8, end = Spacing.spacing4, bottom = Spacing.spacing8),
            style = MaterialTheme.typography.headlineSmall.copy(txtColor),
        )
    }
}

// region Preview
// =============================================================================================================

@Preview(showBackground = true)
@Composable
private fun PreviewSWatchlistItem() {
    val stock = Stock(symbol = "KFC", name = "Kentucky Fried Chicken", isStarred = true)
    WatchlistItem(stock = stock, onStockSelected = { })
}

// endregion ===================================================================================================