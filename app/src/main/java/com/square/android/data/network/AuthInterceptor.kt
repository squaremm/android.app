package com.square.android.data.network

import com.square.android.data.local.LocalDataManager
import okhttp3.Interceptor
import okhttp3.Response

private const val AUTH_HEADER_NAME = "Authorization"

class AuthInterceptor(private val manager: LocalDataManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        if (manager.isTokenPresent()) {
            builder.addHeader(AUTH_HEADER_NAME, manager.getAuthToken())
        }

        return chain.proceed(builder.build())
    }
}