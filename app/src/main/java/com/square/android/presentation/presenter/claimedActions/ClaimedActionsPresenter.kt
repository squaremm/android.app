package com.square.android.presentation.presenter.claimedActions

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.Offer

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PostsLoadedEvent(val offer: Offer)

@InjectViewState
class ClaimedActionsPresenter : BasePresenter<ClaimedActionsView>() {
    private val bus: EventBus by inject()

    init {
        bus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostsLoaded(event: PostsLoadedEvent) {
        processData(event.offer)
    }

    private fun processData(offer: Offer) {
        launch {
            val actions = GlobalScope.async {
                offer.posts.mapTo(HashSet(), Offer.Post::type)
            }

            viewState.showData(actions.await(), offer.credits)
        }
    }

    override fun onDestroy() {
        bus.unregister(this)
    }
}
