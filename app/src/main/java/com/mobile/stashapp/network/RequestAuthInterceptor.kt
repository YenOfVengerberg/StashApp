package com.mobile.stashapp.network

import com.mobile.stashapp.ServerPreference
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RequestAuthInterceptor @Inject constructor(
    private val serverPreference: ServerPreference
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val apiKey = serverPreference.apiKey
        return if (apiKey?.isNotEmpty() == true) {
            val newRequest = chain.request().newBuilder()
                .addHeader("ApiKey", apiKey)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(chain.request())
        }
    }
}