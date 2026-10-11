package com.spotpricepredictor.pro.data.api

import com.spotpricepredictor.pro.data.preferences.ServerSettingsManager
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicHostInterceptor @Inject constructor(
    private val serverSettingsManager: ServerSettingsManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val customUrlString = serverSettingsManager.getBaseUrl()
        val customHttpUrl = customUrlString.toHttpUrlOrNull()

        if (customHttpUrl != null) {
            val newUrl = originalRequest.url.newBuilder()
                .scheme(customHttpUrl.scheme)
                .host(customHttpUrl.host)
                .port(customHttpUrl.port)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(originalRequest)
    }
}
