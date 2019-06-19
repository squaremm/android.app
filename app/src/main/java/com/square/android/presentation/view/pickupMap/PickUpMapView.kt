package com.square.android.presentation.view.pickupMap

import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.view.BaseView

interface PickUpMapView : BaseView {

    fun showInfo(intervals: List<CampaignInterval>, selected: Long)
    fun locate(location: LatLng)

}
