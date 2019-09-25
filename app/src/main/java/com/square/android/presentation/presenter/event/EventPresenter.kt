package com.square.android.presentation.presenter.event

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.event.EventView
import com.square.android.ui.activity.event.EventExtras
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

class SelectedPlaceExtras(val placeId: Long, var placeName: String)
class SelectedPlaceEvent(val data: SelectedPlaceExtras?)

class EventBookEvent

@InjectViewState
class EventPresenter(val event: Event, val place: Place) : BasePresenter<EventView>() {

    private val eventBus: EventBus by inject()

    var locationPoint: LatLng? = null

    var latLng: LatLng? = null
    var address: String? = null
    var distance: Int = 0

    init {
        loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
        }

        updateLocationAndAddress(latLng, address)
    }

    fun bookClicked() = launch {
        viewState.showBookingProgress()

        eventBus.post(EventBookEvent())
    }

    fun exit(){
        router.exit()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        viewState.showData(place)

        address = place.address
        latLng = place.location.latLng()

        router.replaceScreen(SCREENS.EVENT_DETAILS, EventExtras(event, place))

        viewState.hideProgress()
    }

    fun updateLocationAndAddress(mLatLng: LatLng?, mAddress: String?) {
        latLng = mLatLng
        address = mAddress

        if(locationPoint != null && latLng != null){
            val mDistance = latLng!!.distanceTo(locationPoint!!).toInt()

            distance = mDistance

            showLocationInfo()
        }

        viewState.showAddress(address)
    }

    private fun showLocationInfo() {
        viewState.showDistance(distance)
    }
}