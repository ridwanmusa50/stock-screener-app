package io.rid.stockscreenerapp.ui.screen.companyOverview

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.data.MetaData
import io.rid.stockscreenerapp.data.MonthlyStock
import io.rid.stockscreenerapp.data.Stock
import io.rid.stockscreenerapp.data.TimeSeriesData
import io.rid.stockscreenerapp.ui.component.AppErrDialog
import io.rid.stockscreenerapp.ui.component.AppImageBtn
import io.rid.stockscreenerapp.ui.component.AppLoadingDialog
import io.rid.stockscreenerapp.ui.component.AppSnackBar
import io.rid.stockscreenerapp.ui.component.AppTxt
import io.rid.stockscreenerapp.ui.screen.dashboard.StarredAction
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.Dimen.Size.chartHeight
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing
import io.rid.stockscreenerapp.ui.theme.Dimen.Spacing.spacingBetweenGraphDot
import io.rid.stockscreenerapp.ui.theme.Dimen.TxtSize.txtSize12
import io.rid.stockscreenerapp.ui.theme.StockScreenerAppTheme
import io.rid.stockscreenerapp.ui.theme.black01100
import io.rid.stockscreenerapp.ui.theme.fullRoundedCornerShape
import io.rid.stockscreenerapp.ui.theme.gray01100
import io.rid.stockscreenerapp.ui.theme.green01100
import io.rid.stockscreenerapp.ui.util.Utils.formatWithThousandSeparator

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CompanyOverviewScreen(
    symbol: String,
    name: String,
    isStarred: Boolean,
    onBackPreviousScreen: (Boolean) -> Unit,
    companyOverviewViewModel: CompanyOverviewViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val uiState by companyOverviewViewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    var isEditWatchlist by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        companyOverviewViewModel.initialize(symbol)
        companyOverviewViewModel.updateStock(symbol = symbol, name = name, isStarred = isStarred)
    }

    // Show SnackBar when a stock is starred/unstarred
    LaunchedEffect(uiState.lastStarredAction) {
        uiState.lastStarredAction?.let { action ->
            val msgResId = when (action) {
                StarredAction.STARRED -> R.string.snackbar_add_msg
                StarredAction.UNSTARRED -> R.string.snackbar_remove_msg
            }

            snackBarHostState.showSnackbar(context.getString(msgResId))
            companyOverviewViewModel.clearLastStarredAction() // Reset after showing
        }
    }

    uiState.err?.let {
        AppErrDialog(context = context, err = it)
    }

    Scaffold(
        snackbarHost = { AppSnackBar(snackBarHostState) }
    ) {
        CompanyOverviewContent(
            uiState = uiState,
            onCloseClick = { onBackPreviousScreen(isEditWatchlist) },
            onStockStarred = { stock ->
                companyOverviewViewModel.updateStockStar(stock)
                isEditWatchlist = true
            }
        )
    }
}

@Composable
private fun CompanyOverviewContent(
    uiState: StockOverviewUiState,
    onCloseClick: () -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(horizontal = Spacing.spacing16)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility)
            .navigationBarsPadding(),
    ) {
        HeaderSection(stock = uiState.stock, onCloseClick = onCloseClick, onStockStarred = onStockStarred)

        if (uiState.isLoading) {
            AppLoadingDialog()
        } else {
            CurrentPriceSection(currentPrice = uiState.currentPrice)

            Spacer(modifier = Modifier.height(Spacing.spacing32))

            uiState.chartData?.let { chartData ->
                StockChart(chartData = uiState.chartData)
            } ?: ChartPlaceholder()

            CompanyDetailsSection(
                companyOverview = uiState.companyOverview,
                highestStockPrice = uiState.chartData?.validEntries?.maxOfOrNull { it.price!! },
                lowestStockPrice = uiState.chartData?.validEntries?.minOfOrNull { it.price!! }
            )
        }
    }
}

@Composable
private fun HeaderSection(
    stock: Stock?,
    onCloseClick: () -> Unit,
    onStockStarred: (Stock) -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (backBtn, starBtn, nameTxt, symbolTxt) = createRefs()

        AppImageBtn(
            imageResId = R.drawable.ic_close,
            backgroundModifier = Modifier.constrainAs(backBtn) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            },
            imageModifier = Modifier.size(Dimen.Size.icStar),
            contentScale = ContentScale.Fit,
            onClick = onCloseClick
        )

        stock?.let {
            AppImageBtn(
                imageResId = if (it.isStarred) R.drawable.ic_starred else R.drawable.ic_unstar,
                backgroundModifier = Modifier.constrainAs(starBtn) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
                imageModifier = Modifier.size(Dimen.Size.icStar),
                contentScale = ContentScale.Fit,
                onClick = { onStockStarred(it) }
            )

            AppTxt(
                txt = it.name,
                modifier = Modifier.constrainAs(nameTxt) {
                    top.linkTo(backBtn.bottom, margin = Spacing.spacing8)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
            )

            AppTxt(
                txt = "(${it.symbol})",
                modifier = Modifier.constrainAs(symbolTxt) {
                    top.linkTo(nameTxt.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

@Composable
private fun CurrentPriceSection(currentPrice: String?) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacing12, vertical = Spacing.spacing4)
    ) {
        val (txtLabel, txtPrice) = createRefs()

        createHorizontalChain(txtLabel, txtPrice, chainStyle = ChainStyle.Packed)

        AppTxt(
            txtResId = R.string.general_current_price,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.constrainAs(txtLabel) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )

        AppTxt(
            txt = currentPrice ?: stringResource(R.string.general_not_available),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(start = Spacing.spacing4)
                .constrainAs(txtPrice) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Composable
private fun ChartPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = fullRoundedCornerShape.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        AppTxt(
            txtResId = R.string.chart_not_available,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.spacing16)
        )
    }
}

@Composable
fun StockChart(chartData: ChartData) {
    val spacing = 50f
    val pointRadius = 4.dp
    val lineWidth = 3.dp
    val labelOffset = 12f

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = density.run { txtSize12.toPx() }
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
    }
    val lowerValue = remember { chartData.lowerBound }
    val upperValue = remember { chartData.upperBound }
    val path = remember { Path() }
    val fillPath = remember { Path() }

    val canvasWidthDp = spacingBetweenGraphDot * (chartData.allEntries.size - 1).coerceAtLeast(1) + spacing.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.spacing16)
    ) {
        Canvas(
            modifier = Modifier
                .width(canvasWidthDp)
                .height(chartHeight)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = fullRoundedCornerShape.medium
                )
        ) {
            val width = size.width
            val height = size.height
            val spacePerPoint = (width - spacing) / (chartData.allEntries.size - 1).coerceAtLeast(1)

            // Draw X axis labels (dates - abbreviated MM-dd)
            for (i in chartData.dates.indices) {
                val x = spacing + i * spacePerPoint
                val label = chartData.dates[i].substring(5) // "MM-dd"
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    height - 4.dp.toPx(),
                    textPaint
                )
            }

            // Draw Y axis labels (price)
            val yStepCount = 5
            val priceStep = (upperValue - lowerValue) / yStepCount

            for (i in 0..yStepCount) {
                val y = height - spacing - i * (height - spacing) / yStepCount
                val priceLabel = "%.2f".format(lowerValue + i * priceStep)
                drawContext.canvas.nativeCanvas.drawText(
                    priceLabel,
                    30f,
                    y,
                    textPaint
                )
                // Draw horizontal grid lines
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(spacing, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            path.reset()
            fillPath.reset()

            val validEntries = chartData.validEntries

            // Build the main chart path
            if (validEntries.isNotEmpty()) {
                // Move to first point
                val firstX = spacing + validEntries.first().index * spacePerPoint
                val firstNormalizedPrice = (validEntries.first().price!! - lowerValue) / (upperValue - lowerValue)
                val firstY = height - spacing - (firstNormalizedPrice * (height - spacing))
                path.moveTo(firstX, firstY)

                // Draw curves between points
                for (i in 1 until validEntries.size) {
                    val prevIndex = validEntries[i-1].index
                    val currentIndex = validEntries[i].index

                    val prevX = spacing + prevIndex * spacePerPoint
                    val prevNormalizedPrice = (validEntries[i-1].price!! - lowerValue) / (upperValue - lowerValue)
                    val prevY = height - spacing - (prevNormalizedPrice * (height - spacing))

                    val currentX = spacing + currentIndex * spacePerPoint
                    val currentNormalizedPrice = (validEntries[i].price!! - lowerValue) / (upperValue - lowerValue)
                    val currentY = height - spacing - (currentNormalizedPrice * (height - spacing))

                    if (currentIndex - prevIndex > 1) {
                        // Gap detected - draw straight line to current point
                        path.lineTo(currentX, currentY)
                    } else {
                        // Adjacent points - draw smooth curve
                        val controlX = (prevX + currentX) / 2
                        val controlY = (prevY + currentY) / 2
                        path.quadraticTo(controlX, controlY, currentX, currentY)
                    }
                }

                // Explicitly ensure we reach the last point
                val lastIndex = validEntries.last().index
                val lastX = spacing + lastIndex * spacePerPoint
                val lastNormalizedPrice = (validEntries.last().price!! - lowerValue) / (upperValue - lowerValue)
                val lastY = height - spacing - (lastNormalizedPrice * (height - spacing))
                path.lineTo(lastX, lastY)
            }

            // Create and draw fill path (unchanged from your implementation)
            if (validEntries.size >= 2) {
                val firstX = spacing + validEntries.first().index * spacePerPoint
                val lastX = spacing + validEntries.last().index * spacePerPoint

                fillPath.apply {
                    addPath(path)
                    lineTo(lastX, height - spacing)
                    lineTo(firstX, height - spacing)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(green01100, Color.Transparent),
                        endY = height - spacing
                    )
                )
            }

            // Draw the line
            drawPath(
                path = path,
                color = green01100,
                style = Stroke(width = lineWidth.toPx(), cap = StrokeCap.Round)
            )

            // Draw points and price labels only at valid data points
            chartData.validEntries.forEach { entry ->
                val x = spacing + entry.index * spacePerPoint
                val normalizedPrice = (entry.price!! - lowerValue) / (upperValue - lowerValue)
                val y = height - spacing - (normalizedPrice * (height - spacing))

                // Draw point
                drawCircle(
                    color = green01100,
                    radius = pointRadius.toPx(),
                    center = Offset(x, y)
                )

                // Draw price label
                drawContext.canvas.nativeCanvas.drawText(
                    "%.2f".format(entry.price),
                    x,
                    y - labelOffset,
                    textPaint
                )
            }
        }
    }
}

@Composable
private fun CompanyDetailsSection(
    companyOverview: CompanyOverview?,
    highestStockPrice: Float?,
    lowestStockPrice: Float?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.spacing32)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = fullRoundedCornerShape.medium
            )
    ) {
        // First row - Net Assets and Dividend Yield
        DetailRow(
            leftValue = companyOverview?.netAssets?.toFloatOrNull(),
            leftLabel = R.string.stock_net_assets,
            rightValue = companyOverview?.dividendYield?.toFloatOrNull(),
            rightLabel = R.string.stock_dividend_yield,
            formatRightValue = { "%.2f%%".format(it) }
        )

        // Second row - Highest and Lowest Prices
        DetailRow(
            leftValue = highestStockPrice,
            leftLabel = R.string.stock_highest_six_month,
            rightValue = lowestStockPrice,
            rightLabel = R.string.stock_lowest_six_month
        )

        val netAssetsTxt = companyOverview?.inceptionDate?.takeIf { it.isNotBlank() }
            ?: stringResource(R.string.general_not_available)
        DetailItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.spacing12),
            value = netAssetsTxt,
            label = R.string.stock_inception_date
        )
    }
}

@Composable
private fun DetailRow(
    leftValue: Float?,
    leftLabel: Int,
    rightValue: Float?,
    rightLabel: Int,
    formatLeftValue: (Float) -> String = { formatWithThousandSeparator(it) },
    formatRightValue: (Float) -> String = { formatWithThousandSeparator(it) }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.spacing12),
    ) {
        DetailItem(
            modifier = Modifier.weight(1f),
            value = leftValue?.let { formatLeftValue(it) } ?: stringResource(R.string.general_not_available),
            label = leftLabel
        )

        DetailItem(
            modifier = Modifier.weight(1f),
            value = rightValue?.let { formatRightValue(it) } ?: stringResource(R.string.general_not_available),
            label = rightLabel
        )
    }
}

@Composable
private fun DetailItem(
    modifier: Modifier = Modifier,
    value: String,
    label: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppTxt(
            txt = value,
            style = MaterialTheme.typography.titleMedium.copy(black01100)
        )

        AppTxt(
            txtResId = label,
            style = MaterialTheme.typography.bodyMedium.copy(gray01100)
        )
    }
}

// region Preview
// =============================================================================================================

@Preview
@Composable
private fun PreviewCompanyOverviewContentApiReturnedErr() {
    val uiState = StockOverviewUiState(stock = Stock(symbol = "MB", name = "Marrybrown"))
    StockScreenerAppTheme {
        CompanyOverviewContent(uiState = uiState, onCloseClick = { }, onStockStarred = { })
    }
}

@Preview
@Composable
private fun PreviewCompanyOverviewContentApiReturnedSuccess() {
    val uiState = StockOverviewUiState(
        stock = Stock(symbol = "MB", name = "Marrybrown", isStarred = true),
        companyOverview = CompanyOverview(),
        monthlyStock = MonthlyStock(
            metaData = MetaData(),
            monthlyTimeSeries = mapOf(
                "2025-06-05" to TimeSeriesData(
                    open = "100.0",
                    high = "110.0",
                    low = "95.0",
                    close = "105.0",
                    volume = "123456"
                ),
                "2025-05-31" to TimeSeriesData(
                    open = "105.0",
                    high = "115.0",
                    low = "100.0",
                    close = "110.0",
                    volume = "654321"
                ),
                "2025-04-22" to TimeSeriesData(
                    open = "105.0",
                    high = "115.0",
                    low = "100.0",
                    close = "80.0",
                    volume = "654321"
                ),
                "2025-03-22" to TimeSeriesData(
                    open = "105.0",
                    high = "115.0",
                    low = "100.0",
                    close = "80.0",
                    volume = "654321"
                ),
                "2025-02-22" to TimeSeriesData(
                    open = "105.0",
                    high = "115.0",
                    low = "100.0",
                    close = "100.0",
                    volume = "654321"
                ),
                "2025-01-22" to TimeSeriesData(
                    open = "105.0",
                    high = "115.0",
                    low = "100.0",
                    close = "150.0",
                    volume = "654321"
                )
            )
        )
    )
    StockScreenerAppTheme {
        CompanyOverviewContent(uiState = uiState, onCloseClick = { }, onStockStarred = { })
    }
}

@Preview
@Composable
private fun PreviewStockChart() {
    val validEntries = listOf(
        ChartEntry(0, "2025-06-05", 105.0f),
        ChartEntry(2, "2025-04-22", 110.0f),
        ChartEntry(3, "2025-03-22", 80.0f),
        ChartEntry(4, "2025-02-22", 80.0f),
        ChartEntry(5, "2025-01-22", 100.0f),
        ChartEntry(6, "2024-12-22", 150.0f)
    )

    val allEntries = validEntries.toMutableList()
    allEntries.add(ChartEntry(1, "2025-05-31", null))

    StockScreenerAppTheme {
        StockChart(
            chartData = ChartData(
                allEntries = allEntries,
                validEntries = validEntries,
                upperBound = 150.0f,
                lowerBound = 80.0f,
                dates = listOf(
                    "2025-06-05", "2025-05-31", "2025-04-22", "2025-03-22", "2025-02-22", "2025-01-22"
                )
            )
        )
    }
}

@Preview
@Composable
private fun PreviewCompanyDetailsSection() {
    val companyOverview = CompanyOverview(netAssets = "12321121.121", inceptionDate = "1999-03-10")

    StockScreenerAppTheme {
        CompanyDetailsSection(
            companyOverview = companyOverview,
            highestStockPrice = 10000.00212f,
            lowestStockPrice = 5.032f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetailRow() {
    StockScreenerAppTheme {
        DetailRow(
            leftValue = 121.313f,
            leftLabel = R.string.stock_highest_six_month,
            rightValue = 12.3f,
            rightLabel = R.string.stock_lowest_six_month
        )
    }
}

@Preview
@Composable
private fun PreviewDetailItem() {
    StockScreenerAppTheme {
        DetailItem(
            modifier = Modifier.fillMaxWidth().background(Color.White),
            value = "1231,121.0021",
            label = R.string.stock_net_assets
        )
    }
}

// endregion ===================================================================================================