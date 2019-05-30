package com.square.android.presentation.view.fillProfileThird

import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.view.BaseView

interface FillProfileThirdView : BaseView {
    fun showData(profileInfo: ProfileInfo)
}