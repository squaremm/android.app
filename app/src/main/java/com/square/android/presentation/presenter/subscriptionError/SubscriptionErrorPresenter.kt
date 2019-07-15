package com.square.android.presentation.presenter.subscriptionError

import com.arellomobile.mvp.InjectViewState
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.SubscriptionErrorEvent
import com.square.android.presentation.view.subscriptionError.SubscriptionErrorView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

@InjectViewState
class SubscriptionErrorPresenter: BasePresenter<SubscriptionErrorView>(){

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscriptionErrorEvent(event: SubscriptionErrorEvent) {
        if (event.responseType == 1) {
            viewState.finishAc()
        } else {
            viewState.hideProgress()

            if (event.responseType == 2) {
                viewState.showNoConnectionLabel()
            }
        }
    }

    fun checkSubs(){
        viewState.showProgress()
        allowAndCheckSubs()
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
