package com.square.android.presentation.view.fillProfileSecond

import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.view.BaseView

interface FillProfileSecondView : BaseView {
    fun showData(profileInfo: ProfileInfo)
}
