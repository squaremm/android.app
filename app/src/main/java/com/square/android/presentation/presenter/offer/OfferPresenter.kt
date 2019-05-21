package com.square.android.presentation.presenter.offer

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.offer.OfferView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.lang.Exception

class OffersLoadedEvent(val data: List<OfferInfo>)

@InjectViewState
class OfferPresenter : BasePresenter<OfferView>() {

    private var currentPosition = 0

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

    fun itemClicked(position: Int, place: Place?) {
        try{
            currentPosition = position
            viewState.setSelectedItem(position)

            val offer = data!![currentPosition]

            viewState.showOfferDialog(offer, place)

        } catch (e: Exception){


        }

    }

}
