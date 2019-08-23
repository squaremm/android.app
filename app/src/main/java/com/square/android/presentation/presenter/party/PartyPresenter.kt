package com.square.android.presentation.presenter.party

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.*
import com.square.android.extensions.loadImageForIcon
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.view.party.PartyView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class SelectedPlaceExtras(val placeId: Long, val placeName: String, val placeAddress: String, val placeLatLng: LatLng?)
class SelectedPlaceEvent(val data: SelectedPlaceExtras?)

class PartyBookEvent

@InjectViewState
class PartyPresenter(private val partyId: Long) : BasePresenter<PartyView>() {
    var locationPoint: LatLng? = null

    private val eventBus: EventBus by inject()

    var placeLatLng: LatLng? = null

    var data: Place? = null
//    var data: Party? = null

    var placeAddress: String? = null

    //TODO event from PartyPlaceFragment when select clicked
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectedPlaceEvent(event: SelectedPlaceEvent) {
        event.data?.let {
            placeAddress = event.data.placeAddress

            viewState.updateAddressLabel(placeAddress)

            placeLatLng = event.data.placeLatLng

            updateLocationInfo(placeLatLng)
        }
    }

    init {
        eventBus.register(this)
        loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
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

        router.replaceScreen(SCREENS.PARTY_DETAILS, data!!)

        viewState.hideProgress()
    }

    private fun updateLocationInfo(placeLatLng: LatLng?) {
        if(locationPoint != null && placeLatLng != null){
            val distance = placeLatLng.distanceTo(locationPoint!!).toInt()

            data!!.distance = distance

            showLocationInfo()
        }
    }

    private fun showLocationInfo() {
        viewState.showDistance(data!!.distance)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }
}