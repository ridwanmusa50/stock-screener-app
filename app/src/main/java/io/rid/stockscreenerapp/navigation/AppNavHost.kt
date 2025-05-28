package io.rid.stockscreenerapp.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.rid.stockscreenerapp.network.LocalIsNetworkAvailable
import io.rid.stockscreenerapp.ui.component.AppNoInternetDialog
import io.rid.stockscreenerapp.ui.screen.dashboard.DashboardScreen
import io.rid.stockscreenerapp.ui.screen.splash.SplashScreen
import io.rid.stockscreenerapp.ui.theme.defaultEnterTransition
import io.rid.stockscreenerapp.ui.theme.defaultExitTransition
import io.rid.stockscreenerapp.ui.theme.defaultPopEnterTransition
import io.rid.stockscreenerapp.ui.theme.defaultPopExitTransition
import io.rid.stockscreenerapp.ui.theme.verticalEnterTransition
import io.rid.stockscreenerapp.ui.theme.verticalExitTransition
import io.rid.stockscreenerapp.ui.theme.verticalPopEnterTransition
import io.rid.stockscreenerapp.ui.theme.verticalPopExitTransition

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("No NavController provided") }

@Composable
fun AppNavHost() {
    val isOnline = LocalIsNetworkAvailable.current
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = Screen.Splash) {
            setupScreen<Screen.Splash> {
                SplashScreen {
                    navController.navigationWithPopupTo(Screen.Dashboard, Screen.Splash)
                }
            }

            setupScreen<Screen.Dashboard> {
                DashboardScreen()
            }
        }

        AppNoInternetDialog(isShowingDialog = !isOnline)
    }
}

private fun NavHostController.navigationWithPopupTo(destination: Screen, popUpToScreen: Screen) {
    navigate(destination){
        popUpTo(popUpToScreen){ inclusive = true }
    }
}

private inline fun <reified T: Any> NavGraphBuilder.setupScreen(
    screenTransitionType: ScreenTransitionType = ScreenTransitionType.DEFAULT,
    noinline afterSetup: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    val transitions = mapOf(
        ScreenTransitionType.DEFAULT to TransitionProvider(
            enter = { defaultEnterTransition() },
            exit = { defaultExitTransition() },
            popEnter = { defaultPopEnterTransition() },
            popExit = { defaultPopExitTransition() }
        ),
        ScreenTransitionType.VERTICAL to TransitionProvider(
            enter = { verticalEnterTransition() },
            exit = { verticalExitTransition() },
            popEnter = { verticalPopEnterTransition() },
            popExit = { verticalPopExitTransition() }
        )
    )

    val transition = transitions[screenTransitionType]!!

    composable<T>(
        enterTransition = transition.enter,
        exitTransition = transition.exit,
        popEnterTransition = transition.popEnter,
        popExitTransition = transition.popExit,
        content = afterSetup
    )
}

private enum class ScreenTransitionType {
    DEFAULT, VERTICAL
}

private data class TransitionProvider(
    val enter: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition),
    val exit: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition),
    val popEnter: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition),
    val popExit: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)
)