package com.square.android.presentation.view.map

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.Place
import com.square.android.presentation.view.BaseView

interface MapView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPlaces(data: List<Place>)

    fun showInfo(place: Place)
    fun updateCurrentInfoDistance(distance: Int?)
    fun hideInfo()
    fun locate(location: LatLng)
}
