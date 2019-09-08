package com.square.android.presentation.presenter.party

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.party.PartyView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

class SelectedPlaceExtras(val placeId: Long, var placeName: String)
class SelectedPlaceEvent(val data: SelectedPlaceExtras?)

class PartyBookEvent

@InjectViewState
class PartyPresenter(private val partyId: Long) : BasePresenter<PartyView>() {

    private val eventBus: EventBus by inject()

    var data: Place? = null
//    var data: Party? = null

    var locationPoint: LatLng? = null

    var latLng: LatLng? = null
    var address: String? = null

    init {
        loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
        }

        if (data != null) {
            updateLocationInfo()
        }
    }

    fun bookClicked() = launch {
        viewState.showBookingProgress()

        eventBus.post(PartyBookEvent())
    }

    fun exit(){
        router.exit()
    }

    private fun loadData() = launch {
        viewState.showProgress()

        data = repository.getPlace(partyId).await()
        viewState.showPartyData(data!!)

        address = data!!.address
        latLng = data!!.location.latLng()

        router.replaceScreen(SCREENS.PARTY_DETAILS, data!!)

        viewState.hideProgress()
    }

    fun updateLocationInfo() {
        if(locationPoint != null && latLng != null){
            val distance = latLng!!.distanceTo(locationPoint!!).toInt()

            data!!.distance = distance

            showLocationInfo()
        }
    }

    private fun showLocationInfo() {
        viewState.showDistance(data!!.distance)
    }
}