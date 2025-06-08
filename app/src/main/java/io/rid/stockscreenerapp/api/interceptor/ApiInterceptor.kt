package io.rid.stockscreenerapp.api.interceptor

import io.rid.stockscreenerapp.BuildConfig
import io.rid.stockscreenerapp.ui.util.Const
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {

    companion object {
        const val DEMO_API_KEY = "demo"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Real API Access Key only for Production and DevDevelopment
        val apiAccessKey = if (Const.Environment.IS_DEVELOPMENT_DEBUG_BUILD) DEMO_API_KEY
                            else BuildConfig.API_ACCESS_KEY

        // Add the apiKey as a query parameter before call api
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("apikey", apiAccessKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}