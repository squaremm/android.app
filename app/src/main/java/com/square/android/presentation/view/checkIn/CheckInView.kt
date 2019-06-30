package com.square.android.presentation.view.checkIn

import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.Profile
import com.square.android.presentation.view.BaseView

interface CheckInView: BaseView {
    fun showData(data: Offer, user: Profile.User, place: Place)

    fun hideProgress()
}