package com.square.android.presentation.view.subscriptionError

import com.square.android.presentation.view.BaseView

interface SubscriptionErrorView : BaseView {

    fun showNoConnectionLabel()
    fun showProgress()
    fun hideProgress()
    fun finishAc()
}
