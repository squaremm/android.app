package com.square.android.presentation.view.auth

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.BaseView

interface AuthView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAuthDialog(url: String, trigger: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideProgress()
}
