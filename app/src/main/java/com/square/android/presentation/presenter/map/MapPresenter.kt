package com.square.android.presentation.presenter.map

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.Place

import com.square.android.presentation.presenter.BasePresenter

import com.square.android.presentation.view.map.MapView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


@InjectViewState
class MapPresenter : BasePresenter<MapView>() {
    private var locationPoint: LatLng? = null

    private var data: List<Place>? = null

    private var currentInfo: Place? = null

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateDistances()
            }
        }
    }

    private fun updateDistances() {
        launch {
            fillDistances().await()

            if (currentInfo != null) {
                viewState.updateCurrentInfoDistance(currentInfo!!.distance)
            }
        }
    }

    fun loadData() {
        if (data != null) {
            viewState.showPlaces(data!!)
        } else {
            load()
        }
    }

    private fun load() {
        launch {
            data = repository.getPlaces().await()

            if (locationPoint != null) fillDistances().await()

            viewState.showPlaces(data!!)
        }
    }

    private fun fillDistances(): Deferred<Unit> = GlobalScope.async {
        data?.forEach { place ->
            val placePoint = place.location.latLng()

            val distance = placePoint.distanceTo(locationPoint!!).toInt()

            place.distance = distance
        }

        Unit
    }

    fun markerClicked(id: Long) {
        currentInfo = data!!.first { it.id == id }

        viewState.showInfo(currentInfo!!)
    }

    fun mapClicked() {
        currentInfo = null

        viewState.hideInfo()
    }

    fun infoClicked() {
        currentInfo?.let {
            router.navigateTo(SCREENS.PLACE_DETAIL, it.id)
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_FROM_MAP, hashMapOf("id" to it.id.toString())))
        }
    }

    fun locateClicked() {
        locationPoint?.let {
            viewState.locate(it)
        }
    }
}
