package com.square.android.presentation.view.review

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.Offer
import com.square.android.presentation.view.LoadingView

interface ReviewView : LoadingView {
    fun showData(data: Offer, actionTypes: Set<String>, credits: Map<String, Int>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDialog(type: String, coins: Int, index: Int, placeName: String)

    fun disableItem(position: Int)

    fun setSelectedItem(position: Int)

    fun clearSelectedItem()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCongratulations()

    fun showButtons()

    fun initReviewTypes()
}
