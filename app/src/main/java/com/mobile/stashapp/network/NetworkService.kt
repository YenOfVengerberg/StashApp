package com.mobile.stashapp.network

import android.os.Build
import com.mobile.stashapp.ServerPreference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.lang.IllegalStateException
import javax.inject.Inject

class NetworkService @Inject constructor(
    private val serverPreference: ServerPreference,
    private val requestAuthInterceptor: RequestAuthInterceptor
) {

    private val okHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(requestAuthInterceptor)

        builder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        builder.build()
    }

    private lateinit var apiService: ApiService

    fun getApiService(): ApiService {

        if (this@NetworkService::apiService.isInitialized) {
            return apiService
        }

        val baseUrl = serverPreference.getBaseUrl()
            ?: throw IllegalStateException("Server config not available.")

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)
        return apiService
    }

}