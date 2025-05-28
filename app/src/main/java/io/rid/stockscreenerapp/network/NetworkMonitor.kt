package io.rid.stockscreenerapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}

val LocalIsNetworkAvailable: ProvidableCompositionLocal<Boolean> = compositionLocalOf { true }

class NetworkMonitor @Inject constructor(@ApplicationContext private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val connectionState: StateFlow<ConnectionState> = observeConnectivity()
        .stateIn(
            CoroutineScope(Dispatchers.Default + SupervisorJob()),
            SharingStarted.Eagerly,
            getCurrentConnectivityState()
        )

    private fun observeConnectivity(): Flow<ConnectionState> = callbackFlow {
        var lastState: ConnectionState? = null

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (lastState != ConnectionState.Available) {
                    trySend(ConnectionState.Available)
                    lastState = ConnectionState.Available
                }
            }

            override fun onLost(network: Network) {
                if (lastState != ConnectionState.Unavailable) {
                    trySend(ConnectionState.Unavailable)
                    lastState = ConnectionState.Unavailable
                }

            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Emit initial state
        trySend(getCurrentConnectivityState())

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }

    private fun getCurrentConnectivityState(): ConnectionState {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isConnected = capabilities?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
        } == true

        return if (isConnected) ConnectionState.Available else ConnectionState.Unavailable
    }

}