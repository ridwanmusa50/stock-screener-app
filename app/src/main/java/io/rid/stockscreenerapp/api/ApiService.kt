package io.rid.stockscreenerapp.api

import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.data.FilteredStock
import io.rid.stockscreenerapp.data.MonthlyStock
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

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String,
        @Query("symbol") symbol: String
    ): Response<CompanyOverview>

    @GET("query")
    suspend fun getMonthlyStock(
        @Query("function") function: String,
        @Query("symbol") symbol: String
    ): Response<MonthlyStock>

}