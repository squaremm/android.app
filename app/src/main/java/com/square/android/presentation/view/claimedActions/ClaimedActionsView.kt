package com.square.android.presentation.view.claimedActions

import com.square.android.presentation.view.BaseView

interface ClaimedActionsView : BaseView {
    fun showData(actionTypes: Set<String>, credits: Map<String, Int>)
}
