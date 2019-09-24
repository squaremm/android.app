package com.square.android.presentation.view.event

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface EventView : BaseView {
    fun showData(place: Place)

    fun showProgress()

    fun hideProgress()

    fun showBookingProgress()

    fun showBottomView()

    fun hideBottomView()

    fun showDistance(distance: Int?)
}