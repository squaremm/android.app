package com.square.android.presentation.view.aboutPlace

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface AboutPlaceView : BaseView {
    fun showData(place: Place)
    fun showDistance(distance: Int)
}
