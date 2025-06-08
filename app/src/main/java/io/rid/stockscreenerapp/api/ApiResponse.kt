package io.rid.stockscreenerapp.api

import io.rid.stockscreenerapp.ui.util.Const
import java.net.HttpURLConnection

@Suppress("unused")
sealed class ApiResponse<out T> {

    // 200
    data class Success<T>(val data: T) : ApiResponse<T>()

    // 400
    open class BadRequestErr(body: String) : SingleMsgErr(HttpURLConnection.HTTP_BAD_REQUEST, body)

    // 429
    open class TooManyRequestsErr(body: String) : SingleMsgErr(Const.ApiStatusCode.TOO_MANY_REQUESTS, body)

    open class TimeoutErr : Err()

    open class NoNetworkErr : Err()

    open class SingleMsgErr(statusCode: Int, errBody: String?) : Err()

    open class ServerUnavailableErr(errBody: String) : SingleMsgErr(HttpURLConnection.HTTP_UNAVAILABLE, errBody)

    open class Err : ApiResponse<Nothing>()

    class ExceptionErr(e: Throwable) : Err()

}