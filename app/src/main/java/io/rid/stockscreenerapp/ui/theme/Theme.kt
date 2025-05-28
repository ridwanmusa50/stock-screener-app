package io.rid.stockscreenerapp.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    primary = red01100,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    secondary = blue02100,
    onSecondary = Color.White,
    secondaryContainer = Color.White
)

@Composable
fun StockScreenerAppTheme(content: @Composable () -> Unit) {

    CompositionLocalProvider(LocalIndication provides ripple()) {
        MaterialTheme(
            colorScheme = lightScheme,
            typography = AppTypography,
            content = content,
            shapes = Shapes,
        )
    }

}