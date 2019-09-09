package com.square.android.presentation.presenter.review

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.*
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.view.review.ReviewView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

class ActionExtras(var index: Int, var id: String = "", var photo: ByteArray? = null)

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
        loadData()
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
            //TODO get fb user name from API
            viewState.showDialog(index, actions[index], subActions, data!!.instaUser, "TODO")

            //        reviewInfo.postType = type
        }
    }

    fun addAction(index: Int, photo: ByteArray){
        //TODO start from here

        //TODO check action and add it to filledActions
    }

    fun submitClicked() = launch {
        viewState.showLoadingDialog()

      //TODO for every action in filledActions
//        interactor.addReview(ReviewInfo(), offerId, redemptionId, action.photo, action.id ? ).await()

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

}
