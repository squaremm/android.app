package com.square.android.presentation.presenter.pickupMap

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.pickupMap.PickUpMapView

@InjectViewState
class PickUpMapPresenter(var intervals: List<CampaignInterval>, var selected: Long): BasePresenter<PickUpMapView>(){

    private var locationPoint: LatLng? = null

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
        }
    }

    fun loadData(){
        viewState.showInfo(intervals, selected)
    }

    fun locateClicked() {
        locationPoint?.let {
            viewState.locate(it)
        }
    }

    fun markerClicked(intervalId: Long) {
        //TODO post eventBus post event with intervalId to PickUpFragment and navigate back to PickUpFragment
    }

}