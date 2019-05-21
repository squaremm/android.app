package com.square.android.presentation.presenter.main

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.main.MainView
import com.square.android.utils.DeviceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class TutorialInvokeEvent(val data: String?)

class BadgeStateChangedEvent()

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {
    private val bus: EventBus by inject()

    private var currentScreenKey: String? = null

    fun navigationClicked(screenKey: String) {
        if (currentScreenKey != screenKey) {
            router.navigateTo(screenKey)

            bus.post(TutorialInvokeEvent(screenKey))
        }
        currentScreenKey = screenKey

    }

    init {
        launch {
            val user = repository.getCurrentUser().await()
            if (user.isAcceptationPending) {
                viewState.showUserPending()
            } else {
                if (!repository.isLoggedIn() || !repository.isProfileFilled()) {
                    router.replaceScreen(SCREENS.START)
                } else {
                    router.replaceScreen(SCREENS.PLACES)

                    viewState.checkInitial()

                    loadBadgeCount()
                }

            }
        }

        bus.register(this)

        Log.e("LOL", " " + repository.getFcmToken())
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
