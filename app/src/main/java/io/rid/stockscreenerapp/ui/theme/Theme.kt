package io.rid.stockscreenerapp.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    primary = green01100,
    onPrimary = black01100,
    primaryContainer = black03100,
    secondary = pink01100,
    onSecondary = black01100,
    secondaryContainer = black03100,
    surface = Color.White,  // Default background for components like Snackbar
    onSurface = black01100, // Default text color on surface
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