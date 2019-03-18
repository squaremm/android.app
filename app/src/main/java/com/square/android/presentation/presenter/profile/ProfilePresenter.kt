package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.profile.ProfileView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class ProfileUpdatedEvent

@Suppress("unused")
@InjectViewState
class ProfilePresenter : BasePresenter<ProfileView>() {
    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProfileUpdatedEvent(event: ProfileUpdatedEvent) {
        loadData()
    }

    private fun loadData() {
        launch {
            val user = repository.getCurrentUser().await()

            viewState.showUser(user)
        }
    }

    fun openSettings() {
        router.navigateTo(SCREENS.EDIT_PROFILE)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }
}
