package com.square.android.data.network

import android.util.Log
import com.square.android.data.local.LocalDataManager
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class OauthTokenInterceptor(private val manager: LocalDataManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("SUBSCRIPTIONS LOG","INTERCEPTOR -> OauthTokenInterceptor: intercept()")

        manager.setShouldRefreshToken(true)

        val builder = chain.request().newBuilder()

        val expiresIn = manager.getOauthExpires()
        Log.d("SUBSCRIPTIONS LOG","INTERCEPTOR -> OauthTokenInterceptor: intercept() -> expiresIn: $expiresIn")

        val validExpiry = (expiresIn - Calendar.getInstance().timeInMillis) > 999

        if (validExpiry) {
            Log.d("SUBSCRIPTIONS LOG","INTERCEPTOR -> OauthTokenInterceptor: intercept() -> validExpiry, oAuthToken: ${manager.getOauthToken()}")

            builder.header("Authorization", manager.getOauthToken())
        } else{
            Log.d("SUBSCRIPTIONS LOG","INTERCEPTOR -> OauthTokenInterceptor: intercept() -> NOT validExpiry")
        }

        return chain.proceed(builder.build())
    }

}