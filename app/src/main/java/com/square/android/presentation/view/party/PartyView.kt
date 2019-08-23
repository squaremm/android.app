package com.square.android.presentation.view.party

import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface PartyView : BaseView {
    fun showPartyData(party: Place)

    fun showPlaceData(name: String, image: String)

    fun updateAddressLabel(address: String?)

    fun showProgress()

    fun hideProgress()

    fun showBookingProgress()

    fun showBottomView()

    fun hideBottomView()

    fun showDistance(distance: Int?)
}