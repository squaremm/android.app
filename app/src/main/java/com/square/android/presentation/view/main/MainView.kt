package com.square.android.presentation.view.main

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.BaseView

interface MainView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun checkInitial()

    fun setActiveRedemptions(count: Int)

    fun showUserPending()

    fun hideUserPending()
}
