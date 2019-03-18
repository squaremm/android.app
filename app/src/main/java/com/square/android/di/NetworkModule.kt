package com.square.android.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.square.android.Network.BASE_API_URL
import com.square.android.data.network.ApiService
import com.square.android.data.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

private const val MAX_TIMEOUT = 15L


val networkModule = module {
    single { AuthInterceptor(manager = get()) }

    single { createClient(interceptor = get()) }

    single { createRetrofit(get(), BASE_API_URL) }

    single { get<Retrofit>().create(ApiService::class.java) }
}

private fun createClient(interceptor: AuthInterceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(MAX_TIMEOUT, TimeUnit.SECONDS)
        .build()

private fun createRetrofit(okHttp: OkHttpClient, baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttp)
        .build()