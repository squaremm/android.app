package com.square.android.data.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.square.android.App
import com.square.android.BuildConfig
import com.square.android.R
import com.square.android.data.network.response.MessageResponse
import retrofit2.HttpException
import java.lang.Exception
import java.net.UnknownHostException

val Throwable.errorMessage: String
    get() {
        if (BuildConfig.DEBUG)
            printStackTrace()

        return when (this) {
            is HttpException -> processHttp(this)
            is UnknownHostException -> processUnknownHost()
            else -> message ?: App.getString(R.string.unknown_error)
        }
    }

fun processUnknownHost(): String {
    return App.getString(R.string.error_unable_to_connect)
}

fun processHttp(exception: HttpException): String {
    val parsed = tryParseMessage(exception)

    if (parsed != null) return parsed.message

    val resp = exception.response()

    return resp.errorBody()?.string() ?: exception.message()
}

fun tryParseMessage(exception: HttpException): MessageResponse? {
    val mapper  = ObjectMapper()

    return try {
        val error = exception.response().errorBody()!!.string()

        mapper.readValue(error, MessageResponse::class.java)
    } catch (e: Exception) {
        null
    }
}
