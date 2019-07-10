package com.square.android.presentation.view.pickUpSpot

import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.view.BaseView

interface PickUpSpotView : BaseView {
    fun dataLoaded(spots: List<CampaignInterval.Location>)
}
