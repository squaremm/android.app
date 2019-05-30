package com.square.android.presentation.view.fillProfileSecond

import com.mukesh.countrypicker.Country
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.view.BaseView

interface FillProfileSecondView : BaseView {
    fun showDialInfo(country: Country)
    fun showData(profileInfo: ProfileInfo)
}
