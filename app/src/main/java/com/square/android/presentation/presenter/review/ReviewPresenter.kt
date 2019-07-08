package com.square.android.presentation.presenter.review

import com.arellomobile.mvp.InjectViewState
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.data.pojo.CREDITS_TO_SOCIAL
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.ReviewInfo
import com.square.android.data.pojo.TYPE_PICTURE
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.presenter.sendPicture.SendPictureEvent
import com.square.android.presentation.view.review.ReviewView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

@InjectViewState
class ReviewPresenter(private val offerId: Long,
                      private val redemptionId: Long) : BasePresenter<ReviewView>() {

    private val bus: EventBus by inject()

    private val interactor : ReviewInteractor by inject()

    private var data: Offer? = null

    val reviewInfo = ReviewInfo()

    private var currentPosition: Int? = null

    init {
        bus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendPictureEvent(event: SendPictureEvent) {
        lastStageReached(event.data.index, event.data.type)
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = interactor.getOffer(offerId).await()

        val placeId = data!!.place.id

        val feedback = repository.getFeedbackContent(placeId).await()

        reviewInfo.feedback = feedback.message

        viewState.initReviewTypes()

        viewState.hideProgress()
        viewState.showData(data!!, reviewInfo.feedback)
    }

    fun itemClicked(type: String, index: Int) {
        val coins = data!!.credits[type] ?: 0

        reviewInfo.postType = type

        if(reviewInfo.postType == TYPE_PICTURE){

            router.navigateTo(SCREENS.SEND_PICTURE, index)

        } else{
            viewState.showDialog(type, coins, reviewInfo.feedback)
        }
    }

    fun lastStageReached(index: Int, type: Int? = null) {
        currentPosition = index

        createPost(type)
    }

    private fun createPost(type: Int? = null) = launch {

        type?.let {
            //TODO new addReview for sendPicture?

        } ?: run {
            interactor.addReview(reviewInfo, offerId).await()
        }

        viewState.disableItem(currentPosition!!)
        viewState.showButtons()

        currentPosition = null
    }

    fun submitClicked() = launch {
        viewState.showMessage(R.string.claim_progress)

        interactor.claimRedemption(redemptionId, offerId).await()

        sendRedemptionsUpdatedEvent()
        sendBadgeEvent()

        viewState.showCongratulations()
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

    private fun sendBadgeEvent() {
        val event = BadgeStateChangedEvent()

        bus.post(event)
    }

    fun finishChain() = router.finishChain()

    private fun sendRedemptionsUpdatedEvent() {
        val event = RedemptionsUpdatedEvent()
        bus.post(event)
    }

    override fun onDestroy() {
        super.onDestroy()

        bus.unregister(this)
    }
}
