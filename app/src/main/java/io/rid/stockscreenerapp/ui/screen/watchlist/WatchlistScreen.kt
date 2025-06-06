package io.rid.stockscreenerapp.ui.screen.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.ui.component.AppImageBtn
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing

@Composable
fun WatchlistScreen(
    stocks: List<Stock>,
    modifier: Modifier,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (String, Boolean) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(count = stocks.size) { index ->
            val stock = stocks[index]
            FavouriteStock(stock = stock, onStockSelected = onStockSelected, onStockStarred = onStockStarred)
        }
    }
}

@Composable
private fun FavouriteStock(
    stock: Stock,
    onStockSelected: (Stock) -> Unit,
    onStockStarred: (String, Boolean) -> Unit
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
            contentScale = ContentScale.Fit,
            onClick = { onStockStarred(stock.symbol, stock.isStarred) }
        )
    }
}