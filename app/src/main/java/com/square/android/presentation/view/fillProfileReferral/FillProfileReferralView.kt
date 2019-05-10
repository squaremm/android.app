package com.square.android.presentation.view.fillProfileReferral

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.presentation.view.ProgressView

interface FillProfileReferralView : ProgressView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPendingUser()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()
}
