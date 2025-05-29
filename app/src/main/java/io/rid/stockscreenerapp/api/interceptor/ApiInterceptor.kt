package io.rid.stockscreenerapp.api.interceptor

import io.rid.stockscreenerapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Add the apiKey as a query parameter before call api
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("apikey", BuildConfig.API_ACCESS_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}