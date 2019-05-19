package com.square.android.presentation.view.review

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.ProgressView

interface ReviewView : ProgressView {
    fun showData(data: Offer, feedback: String, user: Profile.User, place: Place)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(type: String, coins: Int, feedback: String)

    fun disableItem(position: Int)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun copyFeedbackToClipboard(feedback: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openLink(link: String)

    fun setSelectedItem(position: Int)

    fun clearSelectedItem()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCongratulations()

    fun showButtons()

    fun initReviewTypes()
}
