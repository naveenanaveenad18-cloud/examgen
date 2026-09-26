package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    const val DEFAULT_BASE_URL = "http://10.0.2.2:5000"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS) // AI/paper generation can take time
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var currentBaseUrl: String = DEFAULT_BASE_URL

    @Volatile
    private var cachedService: ExamApiService? = null

    fun getService(baseUrl: String = DEFAULT_BASE_URL): ExamApiService {
        val sanitized = sanitizeUrl(baseUrl)
        if (cachedService != null && currentBaseUrl == sanitized) {
            return cachedService!!
        }

        synchronized(this) {
            if (cachedService != null && currentBaseUrl == sanitized) {
                return cachedService!!
            }
            currentBaseUrl = sanitized
            val retrofit = Retrofit.Builder()
                .baseUrl(sanitized)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val service = retrofit.create(ExamApiService::class.java)
            cachedService = service
            return service
        }
    }

    private fun sanitizeUrl(url: String): String {
        var trimmed = url.trim()
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "http://$trimmed"
        }
        if (!trimmed.endsWith("/")) {
            trimmed = "$trimmed/"
        }
        return trimmed
    }
}
