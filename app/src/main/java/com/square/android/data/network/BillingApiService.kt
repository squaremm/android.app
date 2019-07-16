package com.square.android.data.network

import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import retrofit2.Call
import retrofit2.http.*

interface BillingApiService {

    @GET("purchases/subscriptions/{subscriptionId}/tokens/{token}")
    fun getSubscription(@Path("subscriptionId") subscriptionId: String,
                        @Path("token") token: String) : Call<BillingSubscription>

    @POST("purchases/subscriptions/{subscriptionId}/tokens/{token}:acknowledge")
    fun acknowledgeSubscription(@Path("subscriptionId") subscriptionId: String,
                                @Path("token") token: String,
                                @Body body: TokenInfo): Call<MessageResponse>

    @POST("purchases/subscriptions/{subscriptionId}/tokens/{token}:cancel")
    fun cancelSubscription(@Path("subscriptionId") subscriptionId: String,
                           @Path("token") token: String): Call<MessageResponse>
}