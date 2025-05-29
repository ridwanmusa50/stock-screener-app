package io.rid.stockscreenerapp.api.interceptor

import android.util.Log
import io.rid.stockscreenerapp.api.ApiService
import io.rid.stockscreenerapp.network.ConnectionState
import io.rid.stockscreenerapp.network.NetworkMonitor
import io.rid.stockscreenerapp.ui.util.Const
import kotlinx.coroutines.delay
import retrofit2.Response
import java.io.IOException
import java.io.InterruptedIOException
import java.net.HttpURLConnection
import java.net.UnknownHostException

class Repository(private val apiService: ApiService, private val networkMonitor: NetworkMonitor) {

    // region Repository Setup
    // =============================================================================================================

    suspend fun <T> callApiRepo(call: suspend ApiService.() -> Response<T>): ApiResponse<T> {
        var code = -1
        var errBodyStr = ""

        return try {
            val response = call(apiService)
            code = response.code()

            if (response.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                response.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Success(null as T)
            } else {
                errBodyStr = response.errorBody()?.string() ?: ""
                onErr(code, errBodyStr)
            }
        } catch (e: InterruptedIOException) {
            return fail(code, errBodyStr, "Timeout", ApiResponse.TimeoutErr(), e, isCritical = true)
        } catch (e: IOException) {
            return onIOException(e, code, errBodyStr)
        } catch (e: Throwable) {
            return fail(code, errBodyStr, "Other", e = e, isCritical = true)
        }
    }

    private fun <T> onErr(code: Int, errBodyStr: String): ApiResponse<T> {
        return when (code) {
            HttpURLConnection.HTTP_BAD_REQUEST -> fail(code, errBodyStr, "Bad Request", ApiResponse.BadRequest(errBodyStr))
            Const.ApiStatusCode.TOO_MANY_REQUESTS -> fail(code, errBodyStr, "Too Many Request", ApiResponse.TooManyRequest(errBodyStr))
            HttpURLConnection.HTTP_INTERNAL_ERROR -> fail(code, errBodyStr, "Internal Server Error", ApiResponse.InternalServerErr(errBodyStr))
            else -> fail(code, errBodyStr, "Other", ApiResponse.SingleMsgErr(code, errBodyStr))
        }
    }

    private fun fail(
        code: Int,
        errBodyStr: String,
        errType: String,
        err: ApiResponse.Err = ApiResponse.Err(),
        e: Throwable? = null,
        isCritical: Boolean = false
    ): ApiResponse.Err {
        if (e != null) {
            if (isCritical) Log.e("API", "API Error: $e") else Log.w("API", "API Error: $e")
        }

        Log.w("API", "API Error: $code ${errType}: $errBodyStr")
        return err
    }

    private suspend fun <T> onIOException(
        e: IOException,
        code: Int,
        errBodyStr: String,
    ): ApiResponse<T> {
        val isOffline = networkMonitor.connectionState.value == ConnectionState.Unavailable

        return if (!isOffline) { // No network
            fail(code, errBodyStr, "No Network", ApiResponse.NoNetworkErr(), e)
        } else {
            // Sometimes NetworkMonitorUtil.isOnline is set after the code here is reached,
            // So check the flag again after some delay
            delay(1000L)

            @Suppress("KotlinConstantConditions")
            if (!isOffline) { // No network
                fail(code, errBodyStr, "No Network", ApiResponse.NoNetworkErr(), e)
            } else {
                val errType: String
                val err: ApiResponse.Err

                if (e is UnknownHostException) { // Unable to reach the host with network
                    errType = "Unknown Host"
                    err = ApiResponse.Err()
                } else { // Timeout
                    errType = "Timeout"
                    err = ApiResponse.TimeoutErr()
                }

                fail(code, errBodyStr, errType, err, e, isCritical = true)
            }
        }
    }

    // endregion ===================================================================================================

    // region APIs
    // =============================================================================================================

    // endregion ===================================================================================================

}