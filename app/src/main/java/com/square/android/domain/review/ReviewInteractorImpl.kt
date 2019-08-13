package com.square.android.domain.review

import com.square.android.data.Repository
import com.square.android.data.pojo.*
import com.square.android.extensions.isUrl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ReviewInteractorImpl(private val repository: Repository) : ReviewInteractor {
    override fun addReview(reviewInfo: ReviewInfo, offerId: Long, bookingId: Long, photo: ByteArray?) = GlobalScope.async {
        reviewInfo.link = repository.getUserInfo().socialLink

        repository.addReview(offerId, bookingId, reviewInfo, photo)
    }

    override fun claimRedemption(redemptionId: Long, offerId: Long) = GlobalScope.async {
        repository.claimOffer(redemptionId)

        repository.addOfferToBook(redemptionId, offerId)
    }

    override fun getOffer(id: Long) = GlobalScope.async {
        val offer = repository.getOffer(id).await()

        filterCredits(offer)

        offer
    }

    private fun filterCredits(offer: Offer) {
        offer.credits = offer.credits.filterKeys { key ->
            val socialKey = CREDITS_TO_SOCIAL[key] ?: return@filterKeys false

            val url = offer.place.socials[socialKey]

            if (socialKey == SOCIAL_INSTAGRAM || socialKey == NON_SOCIAL_PHOTO) {
                true
            } else {
                url.isUrl()
            }
        }
    }
}