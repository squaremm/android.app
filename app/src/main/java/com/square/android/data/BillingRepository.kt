package com.square.android.data

import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.*
import kotlinx.coroutines.Deferred

interface BillingRepository {
    fun getSubscription(subscriptionId: String, token: String): Deferred<BillingSubscription?>
    fun acknowledgeSubscription(subscriptionId: String, token: String, tokenInfo: TokenInfo): Deferred<MessageResponse>
    fun cancelSubscription(subscriptionId: String, token: String): Deferred<MessageResponse>
}
