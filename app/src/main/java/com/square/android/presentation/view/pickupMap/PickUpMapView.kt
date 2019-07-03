package com.square.android.presentation.view.pickupMap

import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.CampaignInterval
import com.square.android.data.pojo.CampaignLocationWrapper
import com.square.android.presentation.view.BaseView

interface PickUpMapView : BaseView {

    fun showInfo(locationWrappers: List<CampaignInterval.Location>, selected: Long)
    fun locate(location: LatLng)

}
