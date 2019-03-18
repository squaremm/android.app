package com.square.android.domain.review

import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.ReviewInfo
import kotlinx.coroutines.Deferred

interface ReviewInteractor {
    fun getOffer(id: Long) : Deferred<Offer>

    fun claimRedemption(redemptionId: Long, offerId: Long) : Deferred<MessageResponse>

    fun addReview(reviewInfo: ReviewInfo, offerId: Long) : Deferred<MessageResponse>
}