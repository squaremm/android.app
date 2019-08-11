package com.square.android.presentation.presenter.places

import android.location.Location
import android.text.TextUtils
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
import java.lang.Exception

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var types: MutableList<String> = mutableListOf()
    var filteredTypes: MutableList<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    private var data: List<Place>? = null

    private var filteredData: List<Place>? = null

    var distancesFilled: Boolean = false

    var searchText: CharSequence? = null

    var initialized = false

    init {
        loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateDistances()
            }
        }
    }

    fun filterClicked(position: Int) {
        try{
            val contains = filteredTypes.contains(types[position])

            if(contains){
                filteredTypes.remove(types[position])
            } else{
                filteredTypes.add(types[position])
            }

            viewState.setSelectedFilterItem(position, contains)

            checkFilters()

        } catch (e: Exception){

        }
    }

    fun refreshFilterViews(){
        for (type in filteredTypes) {
            viewState.setSelectedFilterItem(types.indexOf(type), false)
        }
    }

    fun searchTextChanged(text: CharSequence?){
        searchText = text

        checkFilters()
    }

    private fun checkFilters(){
        if(filteredTypes.isEmpty() && TextUtils.isEmpty(searchText)){

            if(distancesFilled){
                data?.let { data = data!!.sortedBy { it.distance } }
            }

            data?.let {viewState.updatePlaces(it) }

        } else{
            data?.let {
                filteredData = data!!.filter {
                    if(filteredTypes.isNotEmpty() && TextUtils.isEmpty(searchText)){
                        it.type in filteredTypes
                    } else if(filteredTypes.isEmpty() && !TextUtils.isEmpty(searchText) ){
                        it.name.contains(searchText.toString(), true)
                    } else{
                        it.type in filteredTypes && it.name.contains(searchText.toString(), true)
                    }
                }

                if(distancesFilled){
                    filteredData?.let { filteredData = filteredData!!.sortedBy { it.distance } }
                }

                filteredData?.let { viewState.updatePlaces(it) }
            }
        }
    }

    private fun updateDistances() {
        launch {
            fillDistances().await()

            viewState.updateDistances()
        }
    }

    private fun loadData() {
        launch {
            viewState.showProgress()

            data = repository.getPlaces().await()

            if (locationPoint != null) fillDistances().await()

            data?.let {
                for(place in it){
                    if(!types.contains(place.type)){
                        types.add(place.type)
                    }
                }
            }

            if(distancesFilled){
                data?.let { data = data!!.sortedBy { it.distance } }
            }

            viewState.hideProgress()
            viewState.showPlaces(data!!, types)

            initialized = true
        }
    }

    fun itemClicked(place: Place) {
        val id = place.id

        if(filteredTypes.isEmpty()){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
            AnalyticsManager.logEvent(AnalyticsEvent(
                    AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = place.name },
                    hashMapOf("id" to id.toString())),
                    repository)

        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
            AnalyticsManager.logEvent(AnalyticsEvent(
                    AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = place.name },
                    hashMapOf("id" to id.toString())),
                    repository)
        }

        router.navigateTo(SCREENS.PLACE_DETAIL, id)
    }

    private fun fillDistances(): Deferred<Unit> = GlobalScope.async {

        if(!distancesFilled){
            distancesFilled = true

            data?.forEach { place ->
                val placePoint = place.location.latLng()

                val distance = placePoint.distanceTo(locationPoint!!).toInt()

                place.distance = distance
            }

            filteredData?.forEach { place ->
                val placePoint = place.location.latLng()

                val distance = placePoint.distanceTo(locationPoint!!).toInt()

                place.distance = distance
            }
        }
    }
}
