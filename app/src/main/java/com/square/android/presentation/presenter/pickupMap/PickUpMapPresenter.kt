package com.square.android.presentation.presenter.pickupMap

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.CampaignInterval
import com.square.android.data.pojo.CampaignLocationWrapper
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.pickUpSpot.IntervalSelectedEvent
import com.square.android.presentation.view.pickupMap.PickUpMapView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class PickUpMapPresenter(var locationWrappers: List<CampaignInterval.Location>, var selected: Long): BasePresenter<PickUpMapView>(){

    private var locationPoint: LatLng? = null

    private val eventBus: EventBus by inject()

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
        }
    }

    fun loadData(){
        viewState.showInfo(locationWrappers, selected)
    }

    fun locateClicked() {
        locationPoint?.let {
            viewState.locate(it)
        }
    }

    fun markerClicked(intervalId: Long) {
        eventBus.post(IntervalSelectedEvent(intervalId))

        back()
    }

    fun back(){
        //TODO check if working
        //TODO ???or router.exit()???
        router.exit()
    }

}