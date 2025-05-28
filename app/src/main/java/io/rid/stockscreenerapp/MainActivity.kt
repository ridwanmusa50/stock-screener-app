package io.rid.stockscreenerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.rid.stockscreenerapp.navigation.AppNavHost
import io.rid.stockscreenerapp.network.ConnectionState
import io.rid.stockscreenerapp.network.LocalIsNetworkAvailable
import io.rid.stockscreenerapp.network.NetworkMonitorViewModel
import io.rid.stockscreenerapp.ui.theme.StockScreenerAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        enableEdgeToEdge(navigationBarStyle = navigationBarStyle)

        setContent()
    }

    private fun setContent() {
        setContent {
            ProvideNetworkStatus {
                AppNavHost()
            }
        }
    }

}

@Composable
fun ProvideNetworkStatus(content: @Composable () -> Unit) {
    val jcNetworkMonitorViewModel = hiltViewModel<NetworkMonitorViewModel>()
    val connectionState by jcNetworkMonitorViewModel.connectionState.collectAsState()
    val isOnline by rememberUpdatedState(connectionState == ConnectionState.Available)

    CompositionLocalProvider(LocalIsNetworkAvailable provides isOnline) {
        StockScreenerAppTheme {
            content()
        }
    }
}