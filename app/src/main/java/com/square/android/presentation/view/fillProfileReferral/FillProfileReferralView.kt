package com.square.android.presentation.view.fillProfileReferral

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.view.BaseView

interface FillProfileReferralView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun sendFcmToken()

    fun showData(profileInfo: ProfileInfo)

    fun showProgress()
    fun hideProgress()
}
