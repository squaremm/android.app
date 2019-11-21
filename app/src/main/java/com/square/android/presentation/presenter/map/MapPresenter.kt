package com.square.android.presentation.presenter.map

import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.places.PlaceSelectedEvent
import com.square.android.presentation.presenter.placesList.PlacesUpdatedEvent
import com.square.android.presentation.view.map.MapView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

@InjectViewState
class MapPresenter(var data: MutableList<Place>): BasePresenter<MapView>() {

    private var locationPoint: LatLng? = null

    private var currentInfo: Place? = null

    private val eventBus: EventBus by inject()

    fun locationGotten(lastLocation: android.location.Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)
        }
    }

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlacesUpdatedEvent(event: PlacesUpdatedEvent) {
        data = event.data

        if(event.shouldUpdateDistances){
            currentInfo?.let {
                val selectedPlace = (data.firstOrNull{ place -> place.id == it.id})

                selectedPlace?.let {
                    it.distance = selectedPlace.distance
                    viewState.updateCurrentInfoDistance(selectedPlace.distance)
                }
            }
        } else{
            mapClicked()
            viewState.updatePlaces(data)
        }

        currentInfo?.let {
            if(it.id !in data.map {place -> place.id}){
                mapClicked()
            }
        }
    }

    fun loadData() {
        viewState.showPlaces(data)
    }

    fun markerClicked(id: Long) {
        currentInfo = data.first { it.id == id }

        viewState.showInfo(currentInfo!!)
    }

    fun mapClicked() {
        currentInfo = null

        viewState.hideInfo()
    }

    fun infoClicked() {
        currentInfo?.let {
            eventBus.post(PlaceSelectedEvent(it, true))
        }
    }

    fun locateClicked() {
        locationPoint?.let {
            viewState.locate(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}
