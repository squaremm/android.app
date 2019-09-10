package com.square.android.domain.review

import com.square.android.data.Repository
import com.square.android.data.pojo.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ReviewInteractorImpl(private val repository: Repository) : ReviewInteractor {
    override fun addReview(offerId: Long, bookingId: Long, actionId: String, photo: ByteArray) = GlobalScope.async {
        val link = repository.getUserInfo().socialLink

        repository.addReview(offerId, bookingId,if(link.isNullOrEmpty()) "" else link, actionId, photo)
    }

    override fun claimRedemption(redemptionId: Long, offerId: Long) = GlobalScope.async {
        repository.claimOffer(redemptionId)

        repository.addOfferToBook(redemptionId, offerId)
    }

    override fun getOffer(id: Long) = GlobalScope.async {
        repository.getOffer(id).await()
    }

}