package com.square.android.presentation.view.claimedActions

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.LoadingView

interface ClaimedActionsView : LoadingView {
    fun showData(actionTypes: Set<String>, credits: Map<String, Int>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(type: String, coins: Int, index: Int)

    fun setSelectedItem(position: Int)

    fun disableItem(position: Int)

    fun initReviewTypes()

    fun clearSelectedItem()
}
