package com.square.android.presentation.view.auth

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.AuthData
import com.square.android.presentation.view.BaseView

interface AuthView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideProgress()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLoginFields()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showRegisterFields()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showForgotFields()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun hideInitialButtons()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPendingUser()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()

}
