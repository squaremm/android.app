package com.square.android.presentation.view.noConnection

import com.square.android.presentation.view.BaseView

interface NoConnectionView : BaseView {
    fun finishAc()

    fun progressVisible(visible: Boolean)
}