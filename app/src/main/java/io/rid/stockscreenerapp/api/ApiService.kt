package io.rid.stockscreenerapp.api

import io.rid.stockscreenerapp.data.FilteredStock
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

    @GET("query")
    suspend fun filterStocks(
        @Query("function") function: String,
        @Query("keywords") keywords: String? = null
    ): Response<FilteredStock>

}