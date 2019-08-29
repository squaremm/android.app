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

    private var filteringMode = 1

    var actualDataLoaded = true

    private var selectedDayPosition: Int? = null

    var types: MutableList<String> = mutableListOf()

    var days: MutableList<String> = mutableListOf()

    var filteredTypes: MutableList<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    private var data: List<Place>? = listOf()

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

            if(actualDataLoaded){
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

    fun dayClicked(position: Int){
        selectedDayPosition = position
        //viewState.setSelectedDayItem(selectedDayPosition)
        checkFilters()
    }

    fun searchTextChanged(text: CharSequence?){
        searchText = text
        checkFilters()
    }

    fun refreshRvForTypes(){
        for (type in filteredTypes) {
            viewState.setSelectedFilterItem(types.indexOf(type), false)
        }
    }

    fun refreshRvForDays(){
        //viewState.setSelectedDayItem(selectedDayPosition)
    }

    //types: 1 - search, 2 - days, 3 - types
    fun changeFiltering(type: Int){
        filteringMode = type
    }

    fun clearFilters(){

    }

    private fun checkFilters() = GlobalScope.async {
        when(filteringMode){
            1 -> {
                if (TextUtils.isEmpty(searchText)) {
                    actualDataLoaded = true
                    fillDistances(true).await()
                    data?.let { viewState.updatePlaces(it) }
                } else {
                    actualDataLoaded = false
                    //TODO get data by text from api as filteredData
                    fillDistances(false).await()
                    filteredData?.let { viewState.updatePlaces(it) }
                }
            }

            2 -> {
                if(filteredTypes.isEmpty()){
                    actualDataLoaded = true
                    fillDistances(true).await()
                    data?.let { viewState.updatePlaces(it) }
                } else {
                    actualDataLoaded = false
                    //TODO get data by date from api as filteredData
                    fillDistances(false).await()
                    filteredData?.let { viewState.updatePlaces(it) }
                }
            }

            3 -> {
                selectedDayPosition?.let {
                    actualDataLoaded = false
                    //TODO get data by type from api as filteredData
                    fillDistances(false).await()
                    filteredData?.let { viewState.updatePlaces(it) }
                } ?: run {
                    actualDataLoaded = true
                    fillDistances(true).await()
                    data?.let { viewState.updatePlaces(it) }
                }
            }
            else -> {}
        }
    }

    private fun updateDistances() {
        launch {
            fillDistances(true).await()

            viewState.updateDistances()
        }
    }

    private fun loadData() {
        launch {
            viewState.showProgress()

//            data = repository.getPlaces().await()
//
//            if (locationPoint != null) fillDistances().await()
//
//            data?.let {
//                for(place in it){
//                    if(!types.contains(place.type)){
//                        types.add(place.type)
//                    }
//                }
//            }

            //TODO remove later
            types.add("Restaurant")
            types.add("Bar")

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

        router.navigateTo(SCREENS.PLACE, id)
    }

    private fun fillDistances(actualData: Boolean): Deferred<Unit?> = GlobalScope.async {
        locationPoint?.let {
            if(actualData) {
                if(!distancesFilled){
                    distancesFilled = true

                    data?.forEach { place ->
                        val placePoint = place.location.latLng()

                        val distance = placePoint.distanceTo(locationPoint!!).toInt()

                        place.distance = distance
                    }
                    data?.let { data = data!!.sortedBy { it.distance } }
                } else{ }
            } else {
                filteredData?.forEach { place ->
                    val placePoint = place.location.latLng()

                    val distance = placePoint.distanceTo(locationPoint!!).toInt()

                    place.distance = distance
                }

                filteredData?.let { filteredData = filteredData!!.sortedBy { it.distance } }
            }
        }
    }
}
