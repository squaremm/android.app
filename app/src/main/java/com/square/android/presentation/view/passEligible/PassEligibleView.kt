package com.square.android.presentation.view.passEligible

import com.square.android.presentation.view.BaseView

interface PassEligibleView : BaseView {
    fun handlePurchases(nullOrEmpty: Boolean)

    fun purchasesComplete()

    fun showDialog()
    fun hideDialog()
}
