package com.square.android.data

import com.square.android.data.local.LocalDataManager
import com.square.android.data.network.BillingApiService
import com.square.android.data.network.response.ERRORS
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.BillingSubscription
import com.square.android.data.pojo.TokenInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ActualBillingRepository(private val api: BillingApiService,
                              private val localManager: LocalDataManager) : BillingRepository {

    override fun getSubscription(subscriptionId: String, token: String): Deferred<BillingSubscription?> = GlobalScope.async {
        val data = performNullableRequest {api.getSubscription(localManager.getOauthToken(),subscriptionId, token)}

        data
    }

    override fun acknowledgeSubscription(subscriptionId: String, token: String, tokenInfo: TokenInfo): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.acknowledgeSubscription(localManager.getOauthToken(),subscriptionId, token, tokenInfo) }

        data
    }

    override fun cancelSubscription(subscriptionId: String, token: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.cancelSubscription(localManager.getOauthToken(),subscriptionId, token) }

        data
    }

    private inline fun <T> performRequest(block: () -> Call<T>): T {
        val response = block().execute()

        if(response is IOException){
            throw HttpException(Response.error<String>(400, ResponseBody.create(null,"" ) ))
        }

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw HttpException(response)
        }
    }

    private inline fun <T> performNullableRequest(block: () -> Call<T>): T? {
        val response = block().execute()

        if(response is IOException){
            return null
        }

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            if(response.code() == 400 ){
                null
            } else{
                throw HttpException(response)
            }
        }
    }

    private inline fun performRequestCheckingMessage(block: () -> Call<MessageResponse>): MessageResponse {
        val result = performRequest(block)

        if (result.message in ERRORS) {
            throw Exception(result.message)
        }

        return result
    }

}