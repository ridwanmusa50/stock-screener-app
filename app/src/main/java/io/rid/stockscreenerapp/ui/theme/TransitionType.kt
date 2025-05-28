package io.rid.stockscreenerapp.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

// region Screen Transition
// =============================================================================================================

fun defaultEnterTransition(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun defaultExitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun defaultPopEnterTransition(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun defaultPopExitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it / 2 },
        animationSpec = tween(durationMillis = 500)
    )
}

fun verticalEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun verticalExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun verticalPopEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { -it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun verticalPopExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(durationMillis = 500)
    )
}

// endregion ===================================================================================================

// region Dialog Transition
// =============================================================================================================

fun alertDialogHalfVerticalEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it / 2 },
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
    )
}

fun alertDialogFullVerticalEnterTransition(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(durationMillis = 500)
    )
}

fun alertDialogVerticalExitTransition(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )
}

// endregion ===================================================================================================