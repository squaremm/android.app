package com.square.android.presentation.presenter.claimedActions

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.claimedCoupon.OfferLoadedEvent
import com.square.android.presentation.presenter.sendPicture.SendPictureEvent
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

//    val reviewInfo = ReviewInfo()

    init {
        bus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendPictureEvent(event: SendPictureEvent) {
        addReview(event.data.index, event.data.photo)
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
            viewState.initReviewTypes()

            val actions = GlobalScope.async {
                offer.posts.mapTo(HashSet(), Offer.Post::type)
            }

            viewState.showData(actions.await(), offer.credits)

            // TODO - new?
//            val placeId = data!!.place.id
//            val feedback = repository.getFeedbackContent(placeId).await()
//            reviewInfo.feedback = feedback.message
//
//            viewState.initReviewTypes()
//            val act = repository.getActions(offer.id, redemptionId).await()
//
//            val actions = act.mapTo(HashSet(), ReviewNetType::type)
//            val credits = hashMapOf(*act.map { it.type to it.credits }.toTypedArray())

//            viewState.showData(actions, credits, reviewInfo.feedback, data!!.instaUser  )
        }
    }

    fun navigateByKey(index: Int, reviewType: String) {

        when(reviewType){
            TYPE_PICTURE -> {
                router.navigateTo(SCREENS.SEND_PICTURE, index)
            }
            TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY -> {
                addReview(index)
            }
            else -> {
                router.navigateTo(SCREENS.UPLOAD_SCREENSHOT, index)
            }
        }
    }

    private fun addReview(index: Int, photo: ByteArray? = null) = launch {
        viewState.showLoadingDialog()

        currentPosition = index

//        interactor.addReview(reviewInfo, offerId, redemptionId, photo).await()

        viewState.disableItem(currentPosition!!)

        currentPosition = null

        viewState.hideLoadingDialog()
    }

    fun itemClicked(type: String, index: Int) {
        val coins = data!!.credits[type] ?: 0

//        reviewInfo.postType = type

        viewState.showDialog(type, coins, index)
    }

    override fun onDestroy() {
        bus.unregister(this)
    }
}
