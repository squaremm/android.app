package com.square.android.presentation.presenter.claimedRedemption

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Offer
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.claimedCoupon.OfferLoadedEvent
import com.square.android.presentation.view.claimedRedemption.ClaimedRedemptionView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class ClaimedRedemptionPresenter(
        private val offerId: Long,
        private val redemptionId: Long) : BasePresenter<ClaimedRedemptionView>() {

    private val bus: EventBus by inject()

    init {
        loadData()
    }

    private fun loadData() {
        launch {
            val offer = repository.getOffer(offerId).await()

            sendEvents(offer)
        }
    }

    private fun sendEvents(offer: Offer) {
        val couponEvent = OfferLoadedEvent(offer, redemptionId)

        bus.post(couponEvent)
    }

}
