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
import retrofit2.Call
import retrofit2.HttpException

class ActualBillingRepository(private val api: BillingApiService,
                              private val localManager: LocalDataManager) : BillingRepository {

    override fun getSubscription(subscriptionId: String, token: String): Deferred<BillingSubscription> = GlobalScope.async {
        val data = performRequest { api.getSubscription(localManager.getOauthToken(),subscriptionId, token) }

        data
    }

    override fun acknowledgeSubscription(subscriptionId: String, token: String, tokenInfo: TokenInfo): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.acknowledgeSubscription(localManager.getOauthToken(),subscriptionId, token, tokenInfo) }

        data
    }

    private inline fun <T> performRequest(block: () -> Call<T>): T {
        val response = block().execute()

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw HttpException(response)
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