package io.rid.stockscreenerapp.api

import android.content.Context
import io.rid.stockscreenerapp.BuildConfig
import io.rid.stockscreenerapp.api.interceptor.ApiInterceptor
import jakarta.inject.Inject
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ApiServiceBuilder @Inject constructor(context: Context) {

    companion object {
        const val TIMEOUT_SECONDS = 30L
    }

    private val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB cache
    private val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_API_URL)
        .client(createOkHttpClient().build())
        .addConverterFactory(ScalarsConverterFactory.create()) // To handle non-Json response: plain strings, CSVs, etc.
        .addConverterFactory(GsonConverterFactory.create()) // To handle JSON response
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    fun build(): Retrofit {
        return builder.build()
    }

    fun createOkHttpClient(): OkHttpClient.Builder {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

        // Dispatcher
        val dispatcher = Dispatcher().apply { maxRequests = 5 }

        okHttpClientBuilder.apply {
            dispatcher(dispatcher)

            // Timeout
            readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            // Cache
            cache(cache)

            // Interceptors
            addInterceptor(ApiInterceptor())
        }

        return okHttpClientBuilder
    }

}