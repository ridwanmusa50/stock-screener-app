package io.rid.stockscreenerapp.network

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NetworkMonitorViewModel @Inject constructor(networkMonitorUtils: NetworkMonitor) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> = networkMonitorUtils.connectionState

}