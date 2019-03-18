package com.square.android.presentation.presenter.claimedCoupon

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.PlaceInfo

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.claimedCoupon.ClaimedCouponView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class OfferLoadedEvent(val offer: Offer, val redemptionId: Long)

@InjectViewState
class ClaimedCouponPresenter : BasePresenter<ClaimedCouponView>() {
    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOfferLoaded(event: OfferLoadedEvent) {
        launch {
            val userInfo = repository.getUserInfo()
            val redemption = repository.getRedemption(event.redemptionId).await()

            viewState.showData(event.offer, redemption.redemption.place, userInfo)
        }
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }
}
