package com.square.android.presentation.view.claimedActions

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.Offer
import com.square.android.presentation.view.LoadingView

interface ClaimedActionsView : LoadingView {
    fun showData(data: Offer, actions: List<Offer.Action>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(index: Int, action: Offer.Action,subActions: List<Offer.Action>, instaName: String, fbName: String )

    fun disableAction(position: Int)
}
