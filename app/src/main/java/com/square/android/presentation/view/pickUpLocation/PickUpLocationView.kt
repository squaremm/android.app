package com.square.android.presentation.view.pickUpLocation

import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.view.BaseView

interface PickUpLocationView : BaseView {

    fun showData(location: CampaignInterval.Location)
}
