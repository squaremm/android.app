package com.square.android.presentation.presenter.places

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.places.PlacesView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {
    init {
        loadData()
    }

    var types: MutableList<String> = mutableListOf()
    var filteredTypes: List<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    private var data: List<Place>? = null

    private var filteredData: List<Place>? = null

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateDistances()
            }
        }
    }

    fun saveClicked(selectedTypes: List<String>){
        filteredTypes = selectedTypes

        if(filteredTypes.isEmpty()){
            data?.let {viewState.updatePlaces(it) }
        } else{
            data?.let {
                filteredData = data!!.filter { it.type in filteredTypes }

                filteredData?.let { viewState.updatePlaces(it) }
            }
        }

        viewState.showBadge(filteredTypes.size)
    }

    private fun updateDistances() {
        launch {
            fillDistances().await()

            viewState.updateDistances()
        }
    }

    private fun loadData() {
        launch {
            data = repository.getPlaces().await()

            if (locationPoint != null) fillDistances().await()

            data?.let {
                for(place in it){
                    if(!types.contains(place.type)){
                        types.add(place.type)
                    }
                }
            }

            viewState.hideProgress()
            viewState.showPlaces(data!!, types)
        }
    }

    fun itemClicked(position: Int) {
        val id = data!![position].id

        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED.apply { venueName = data!![position].name }, hashMapOf("id" to id.toString())), repository)
        AnalyticsManager.logEvent(AnalyticsEvent(
                AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = data!![position].name },
                hashMapOf("id" to id.toString())),
            repository)

        router.navigateTo(SCREENS.PLACE_DETAIL, id)
    }


    private fun fillDistances(): Deferred<Unit> = GlobalScope.async {
        data?.forEach { place ->
            val placePoint = place.location.latLng()

            val distance = placePoint.distanceTo(locationPoint!!).toInt()

            place.distance = distance
        }

        Unit
    }
}
