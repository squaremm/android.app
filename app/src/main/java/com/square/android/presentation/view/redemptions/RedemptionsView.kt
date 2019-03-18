package com.square.android.presentation.view.redemptions

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.ProgressView

interface RedemptionsView : ProgressView {
    fun showData(ordered: List<Any>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun removeItem(position: Int)
}
