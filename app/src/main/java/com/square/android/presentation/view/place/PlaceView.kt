package com.square.android.presentation.view.place

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface PlaceView : BaseView {
    fun showData(place: Place)
    fun showDistance(distance: Int?)
}