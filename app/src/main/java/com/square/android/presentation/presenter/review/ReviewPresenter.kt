package com.square.android.presentation.presenter.review

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.*
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

class ActionExtras(var index: Int, var id: String = "", var photo: ByteArray? = null, var type: String = "")

@InjectViewState
class ReviewPresenter(private val offerId: Long,
                      private val redemptionId: Long) : BasePresenter<ReviewView>() {

    private val bus: EventBus by inject()

    private val interactor : ReviewInteractor by inject()

    private var data: Offer? = null

    private var actions: List<Offer.Action> = listOf()
    var subActions: List<Offer.Action> = listOf()

    private val filledActions: MutableList<ActionExtras> = mutableListOf()

    init {
        bus.register(this)

        loadData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendPictureEvent(event: SendPictureEvent) {

        //TODO send action data and add action to  filledActions
//        addReview(event.data.index, event.data.photo)
//        viewState.showButtons()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = interactor.getOffer(offerId).await()

        actions = data!!.actions
        subActions = data!!.subActions

        for(subAction in subActions){
            if(subAction.attempts >= subAction.maxAttempts) subAction.enabled = false
        }

        for(action in actions){
            if(action.type != TYPE_PICTURE){
                if(action.attempts >= action.maxAttempts) action.enabled = false
            } else{
                var disabledCount = 0
                for(subAction in subActions){
                    if(!subAction.enabled) disabledCount++
                }
                if(disabledCount == subActions.size) action.enabled = false
            }
        }

        viewState.hideProgress()
        viewState.showData(data!!, actions)
    }

    fun itemClicked(index: Int) {
        if(index in filledActions.map { it.index }){
            //TODO show dialog if user is sure to delete this action. If yes - delete this action from filledActions and fire adapter.changeSelection(index)
            //TODO then check if filledActions is empty, if is empty - viewState.hideButtons()
        } else{
            // viewState.showDialog(type, coins, index)
        }
//        reviewInfo.postType = type
    }

    fun submitClicked() = launch {
        viewState.showLoadingDialog()

      //TODO for every action in filledActions
//        interactor.addReview(ReviewInfo(), offerId, redemptionId, action.photo, action.type or action.id ? ).await()


//        interactor.claimRedemption(redemptionId, offerId).await()
//
//        sendRedemptionsUpdatedEvent()
//        sendBadgeEvent()

        viewState.hideLoadingDialog()

        viewState.showCongratulations()
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
