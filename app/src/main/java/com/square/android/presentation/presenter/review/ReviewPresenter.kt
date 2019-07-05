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
import com.square.android.presentation.view.review.ReviewView
import org.greenrobot.eventbus.EventBus
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
        loadData()
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
            //TODO check if working - if not, make and fire method in SelectOfferActivity to router.navigate

            //TODO find a way to back to ReviewFragment from SendPictureActivity
            router.navigateTo(SCREENS.SEND_PICTURE, index)
        } else{
            viewState.showDialog(type, coins, reviewInfo.feedback)
        }
    }

    //TODO fire presenter.lastStageReached(index of send picture) on event bus event from SendPictureActivity when picture uploaded correctly

    fun lastStageReached(index: Int) {
        currentPosition = index

        createPost()
    }

    private fun createPost() = launch {
        interactor.addReview(reviewInfo, offerId).await()

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

    //TODO where to go from here?
    fun goToMain() = router.replaceScreen(SCREENS.REDEMPTIONS)

    private fun sendRedemptionsUpdatedEvent() {
        val event = RedemptionsUpdatedEvent()
        bus.post(event)
    }
}
