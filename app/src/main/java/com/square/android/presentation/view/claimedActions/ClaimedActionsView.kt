package com.square.android.presentation.view.claimedActions

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.BaseView

interface ClaimedActionsView : BaseView {
    fun showData(actionTypes: Set<String>, credits: Map<String, Int>, feedback: String, instaUser: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(type: String, coins: Int, feedback: String)

    fun setSelectedItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openLink(link: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun copyFeedbackToClipboard(feedback: String)

    fun disableItem(position: Int)

    fun initReviewTypes()

    fun clearSelectedItem()
}
