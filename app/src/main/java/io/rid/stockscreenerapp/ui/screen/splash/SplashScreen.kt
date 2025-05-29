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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.network.LocalIsNetworkAvailable
import io.rid.stockscreenerapp.ui.component.AppLoadImage
import io.rid.stockscreenerapp.ui.theme.Dimen
import io.rid.stockscreenerapp.ui.theme.StockScreenerAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private const val ANIMATION_DURATION = 500
private const val SPLASH_DELAY = 2000L

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    val isNetworkAvailable = LocalIsNetworkAvailable.current
    if (isNetworkAvailable) SplashAnimation(onAnimationComplete = onNavigate)
}

@Composable
private fun SplashAnimation(isPreview: Boolean = false, onAnimationComplete: () -> Unit) {
    val animationState = remember { MutableTransitionState(isPreview).apply { targetState = true } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Navigate to next screen will done after animation end and display for 2s
        LaunchedEffect(animationState) {
            snapshotFlow { animationState.isIdle && animationState.currentState }
                .filter { it }
                .first()
            delay(SPLASH_DELAY)
            onAnimationComplete()
        }

        // To animate logo from bottom to middle
        AnimatedVisibility(
            visibleState = animationState,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION)) +
                    slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(ANIMATION_DURATION))
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