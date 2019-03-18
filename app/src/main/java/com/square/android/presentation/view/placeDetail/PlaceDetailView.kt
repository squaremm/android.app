package com.square.android.presentation.view.placeDetail

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface PlaceDetailView : BaseView {
    fun showData(place: Place)
    fun showDistance(distance: Int?)
}
