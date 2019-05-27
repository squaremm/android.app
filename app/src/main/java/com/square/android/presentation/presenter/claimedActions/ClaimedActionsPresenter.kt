package com.square.android.presentation.presenter.claimedActions

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.CREDITS_TO_SOCIAL
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.ReviewInfo
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.claimedCoupon.OfferLoadedEvent
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

@InjectViewState
class ClaimedActionsPresenter: BasePresenter<ClaimedActionsView>() {
    private val bus: EventBus by inject()

    private var currentPosition: Int? = null

    private var redemptionId: Long = 0
    private var offerId: Long = 0

    private val interactor : ReviewInteractor by inject()

    private var data: Offer? = null

    val reviewInfo = ReviewInfo()

    init {
        bus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOfferLoaded(event: OfferLoadedEvent) {

        data = event.offer
        redemptionId = event.redemptionId
        offerId = event.offer.id

        data?.let { processData(data!!)}
    }

    private fun processData(offer: Offer) {
        launch {

            val placeId = data!!.place.id
            val feedback = repository.getFeedbackContent(placeId).await()
            reviewInfo.feedback = feedback.message

            viewState.initReviewTypes()

            val actions = GlobalScope.async {
                offer.posts.mapTo(HashSet(), Offer.Post::type)
            }

            viewState.showData(actions.await(), offer.credits,reviewInfo.feedback, data!!.instaUser  )
        }
    }

    private fun createPost() {
        launch {
            interactor.addReview(reviewInfo, offerId).await()

            viewState.disableItem(currentPosition!!)

            currentPosition = null
        }
    }

    fun lastStageReached(index: Int) {
        currentPosition = index

        createPost()
    }

    fun itemClicked(type: String) {
        val coins = data!!.credits[type] ?: 0

        reviewInfo.postType = type

        viewState.showDialog(type, coins, reviewInfo.feedback)
    }

    fun ratingUpdated(rating: Int) {
        reviewInfo.stars = rating
    }

    fun copyClicked() {
        viewState.copyFeedbackToClipboard(reviewInfo.feedback)
    }

    fun openLinkClicked(type: String) {
        val socialType = CREDITS_TO_SOCIAL[type] ?: return

        val link = data!!.place.socials[socialType] ?: return

        viewState.openLink(link)
    }

    override fun onDestroy() {
        bus.unregister(this)
    }
}
