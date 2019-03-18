package com.square.android.presentation.presenter.main

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.main.MainView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class BadgeStateChangedEvent()

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {
    private val bus: EventBus by inject()

    fun navigationClicked(screenKey: String) {
        if (screenKey != SCREENS.PROFILE) {
            router.replaceScreen(screenKey)
        } else {
            router.navigateTo(screenKey)
        }
    }

    init {
        if (!repository.isLoggedIn() || !repository.isProfileFilled()) {
            router.replaceScreen(SCREENS.START)
        } else {
            router.replaceScreen(SCREENS.PLACES)

            viewState.checkInitial()

            loadBadgeCount()
        }


        bus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBadgeStateChanged(event: BadgeStateChangedEvent) {
        loadBadgeCount()
    }

    override fun onDestroy() {
        super.onDestroy()

        bus.unregister(this)
    }

    private fun loadBadgeCount() {
        launch {
            val count = repository.getBadgeCount().await()

            viewState.setActiveRedemptions(count.activeBooks)
        }
    }
}
