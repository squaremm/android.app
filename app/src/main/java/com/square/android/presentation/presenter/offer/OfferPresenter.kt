package com.square.android.presentation.presenter.offer

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.OfferInfo

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.offer.OfferView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class OffersLoadedEvent(val data: List<OfferInfo>)

@InjectViewState
class OfferPresenter : BasePresenter<OfferView>() {
    private val eventBus: EventBus by inject()

    private var data: List<OfferInfo>? = null

    init {
        eventBus.register(this)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOffersLoadedEvent(event: OffersLoadedEvent) {
        data = event.data

        viewState.showData(event.data)
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
