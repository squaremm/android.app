package com.square.android.presentation.presenter.claimedActions

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.*
import com.square.android.domain.review.ReviewInteractor
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.claimedCoupon.OfferLoadedEvent
import com.square.android.presentation.view.claimedActions.ClaimedActionsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

@InjectViewState
class ClaimedActionsPresenter: BasePresenter<ClaimedActionsView>() {

    private val bus: EventBus by inject()

    private var redemptionId: Long = 0
    private var offerId: Long = 0

    private val interactor : ReviewInteractor by inject()

    private var data: Offer? = null

    private var actions: List<Offer.Action> = listOf()
    var subActions: List<Offer.Action> = listOf()

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

    private fun processData(data: Offer) {
        launch {
            actions = data.actions
            subActions = data.subActions

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

            viewState.showData(data, actions)
        }
    }

    fun addReview(index: Int, photo: ByteArray) = launch {
        viewState.showLoadingDialog()

        //TODO error: D/OkHttp: <-- HTTP FAILED: javax.net.ssl.SSLException: Write error: ssl=0x7b6ed76208: I/O error during system call, Broken pipe
        //TODO changed ReviewInfo to link:String in api.addReview
        interactor.addReview(offerId, redemptionId, actions[index].id, photo).await()

        actions[index].attempts++
        if(actions[index].attempts >= actions[index].maxAttempts){
            actions[index].enabled = false
            viewState.disableAction(index)
        }

        viewState.hideLoadingDialog()
    }

    fun itemClicked(index: Int) {
        viewState.showDialog(index, actions[index], subActions, data!!.instaUser, "TODO")
    }

    override fun onDestroy() {
        bus.unregister(this)
    }
}
