package com.square.android.presentation.view.review

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.Offer
import com.square.android.presentation.view.LoadingView

interface ReviewView : LoadingView {
    fun showData(data: Offer, actions: List<Offer.Action>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(index: Int, action: Offer.Action,subActions: List<Offer.Action>, instaName: String, fbName: String )

    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCongratulations()

    fun showButtons()

    fun hideButtons()
}
