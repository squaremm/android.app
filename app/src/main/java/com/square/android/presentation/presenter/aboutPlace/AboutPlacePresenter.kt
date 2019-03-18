package com.square.android.presentation.presenter.aboutPlace

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.aboutPlace.AboutPlaceView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class AboutLoadedEvent(val place: Place)

class DistanceUpdatedEvent(val distance: Int?)

@InjectViewState
class AboutPlacePresenter : BasePresenter<AboutPlaceView>() {
    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAboutLoadedEvent(event: AboutLoadedEvent) {
        viewState.showData(event.place)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDistanceUpdatedEvent(event: DistanceUpdatedEvent) {
        if (event.distance != null) {
            viewState.showDistance(event.distance)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
