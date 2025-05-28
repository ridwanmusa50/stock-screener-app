package io.rid.stockscreenerapp.ui.screen.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.network.LocalIsNetworkAvailable
import io.rid.stockscreenerapp.ui.component.AppLoadImage
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.StockScreenerAppTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    // Handle network changes without recomposing the UI
    if (!LocalIsNetworkAvailable.current) return

    SplashAnimation(onAnimationComplete = onNavigate)
}

@Composable
private fun SplashAnimation(isPreview: Boolean = false, onAnimationComplete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary),
        contentAlignment = Alignment.Center
    ) {
        val animationState = remember { MutableTransitionState(isPreview).apply { targetState = true } }

        // Navigate to next screen will done after animation end and display for 2s
        LaunchedEffect(animationState.isIdle && animationState.currentState) {
            if (animationState.isIdle && animationState.currentState) {
                delay(2000)
                onAnimationComplete()
            }
        }

        // To animate logo from bottom to middle
        AnimatedVisibility(
            visibleState = animationState,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(500))
        ) {
            AppLoadImage(
                imageResId = R.drawable.ic_logo,
                modifier = Modifier.padding(horizontal = Dimen.Spacing.spacing80)
            )
        }
    }
}

// region Preview
// =============================================================================================================

@Preview
@Composable
private fun PreviewSplashAnimation() {
    StockScreenerAppTheme {
        SplashAnimation(isPreview = true) { }
    }
}

// endregion ===================================================================================================