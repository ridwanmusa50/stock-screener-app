package io.rid.stockscreenerapp.api

import io.rid.stockscreenerapp.ui.util.Const
import java.net.HttpURLConnection

sealed class ApiResponse<out T> {

    // 200
    data class Success<T>(val data: T) : ApiResponse<T>()

    // 400
    open class BadRequest(errBody: String) : SingleMsgErr(HttpURLConnection.HTTP_BAD_REQUEST, errBody)

    // 429
    open class TooManyRequest(errBody: String) : SingleMsgErr(Const.ApiStatusCode.TOO_MANY_REQUESTS, errBody)

    // 500
    open class InternalServerErr(errBody: String) : SingleMsgErr(HttpURLConnection.HTTP_INTERNAL_ERROR, errBody)

    open class TimeoutErr : Err()

    open class NoNetworkErr : Err()

    open class SingleMsgErr(code: Int, errBody: String) : Err()

    open class Err : ApiResponse<Nothing>()

    @Suppress("UNUSED_PARAMETER")
    class ExceptionErr(e: Throwable) : Err()

}