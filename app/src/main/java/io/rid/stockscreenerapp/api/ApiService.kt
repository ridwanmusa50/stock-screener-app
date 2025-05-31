package io.rid.stockscreenerapp.api

import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("query")
    suspend fun getStocks(
        @Query("function") function: String,
        @Query("date") date: String? = null,
        @Query("state") state: String? = null
    ): Response<ResponseBody>

}