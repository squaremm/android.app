package com.square.android.presentation.presenter.places

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.City
import com.square.android.data.pojo.FilterTimeframe
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.places.PlacesView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import java.lang.Exception
import java.util.*

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var filteringMode = 1

    var actualDataLoaded = true

    private var selectedDayPosition: Int? = null

    var types: MutableList<String> = mutableListOf()

    var days: MutableList<String> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var timeframes: MutableList<FilterTimeframe> = mutableListOf()

    var cities: List<City>? = null

    var selectedCity: City? = null

    var allFilters: MutableList<String> = mutableListOf()
    var allSelected: MutableList<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    private var data: List<Place>? = null

    private var filteredData: List<Place>? = null

    var distancesFilled: Boolean = false

    var searchText: CharSequence? = null

    var initialized = false

    var locationInitialized = false

    init {
        loadData()
    }

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            if(!locationInitialized){
                locationInitialized = true

                locationPoint = LatLng(it.latitude, it.longitude)

                if(actualDataLoaded){
                    updateDistances()
                }
            }
        }
    }

    fun filterClicked(position: Int) {
        try{
            val isTimeframe = timeframes.firstOrNull{it.name == allFilters[position]} != null

            if(isTimeframe){
                if(allFilters[position] in allSelected){
                    allSelected.remove(allFilters[position])
                } else{
                    allSelected.add(allFilters[position])
                }

                viewState.updateFilters(allFilters, allSelected, false)

            } else{
                val contains = allSelected.contains(allFilters[position])
                val currentTimeframes = timeframes.filter { it.type == allFilters[position] }.map { it.name }

                if(contains){
                    allSelected.remove(allFilters[position])
                    if(currentTimeframes.isNotEmpty()){

                        val timeframesToRemove: MutableList<String> = mutableListOf()

                        val allTypesWithThoseTimeframes: MutableList<List<String>> = mutableListOf()

                        for(currentTimeframe in currentTimeframes){
                            val list = timeframes.filter { it.name == currentTimeframe}.map{ it.type }

                            allTypesWithThoseTimeframes.add(list)
                        }

                        for(x in 0 until currentTimeframes.size){
                            if(allSelected.firstOrNull{it in allTypesWithThoseTimeframes[x]} == null){
                                timeframesToRemove.add(currentTimeframes[x])
                            }
                        }

                        for(timeframe in timeframesToRemove){
                            allFilters.remove(timeframe)
                            allSelected.remove(timeframe)
                        }
                    }
                } else{
                    allSelected.add(allFilters[position])

                    for(timeframe in currentTimeframes){
                        if(!allFilters.contains(timeframe)){
                            allFilters.add(timeframe)
                        }
                    }
                }

                viewState.updateFilters(allFilters, allSelected, true)
            }

            checkFilters()

        } catch (e: Exception){

        }
    }

    fun citySelected(c: City) = launch {
        viewState.changeCityName(c.name)

        selectedCity = c

        data = repository.getPlacesByFilters(PlaceData().apply { city = selectedCity!!.name }).await()

        distancesFilled = false

        checkFilters()
    }

    fun dayClicked(position: Int){
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition!!)
        checkFilters()
    }

    fun searchTextChanged(text: CharSequence?) {
        searchText = text
        checkFilters()
    }

    //mode: 1 - search, 2 - date, 3 - types
    fun changeFiltering(mode: Int) {
        filteringMode = mode

        checkFilters()
    }

    fun clearFilters() {
        viewState.hideClear()

        allSelected.clear()
        allFilters.removeAll { it in timeframes.map { it.name } }

        viewState.updateFilters(allFilters, allSelected, true)

        setActualData()
    }

    //filteringMode: 1 - search, 2 - date, 3 - types
    private fun checkFilters() = launch {
        when(filteringMode){
            1 -> {
                if (TextUtils.isEmpty(searchText)) {
                    setActualData()
                } else {
                    if(data != null){
                        actualDataLoaded = false

                        filteredData = data!!.filter { it.name.contains(searchText.toString(), true) && it.city == selectedCity?.name }

                        fillDistances(false)
                        filteredData?.let { viewState.updatePlaces(it) }
                    } else {}
                }
            }

            2 -> {
                selectedDayPosition?.let {
                    actualDataLoaded = false

                    var mDate = actualDates[selectedDayPosition!!]

                    filteredData = repository.getPlacesByFilters(PlaceData().apply {
                        date  = mDate
                        selectedCity?.let { city = selectedCity!!.name } }).await()

                    fillDistances(false)
                    filteredData?.let { viewState.updatePlaces(it) }
                } ?: run {
                    setActualData()
                }
            }

            3 -> {
                if(data != null){
                    if(allSelected.isEmpty()){
                        viewState.hideClear()
                        setActualData()
                    } else {
                        actualDataLoaded = false

                        val selectedTimeframes = timeframes.filter { it.name in allSelected }
                        val selectedTypes = allSelected - selectedTimeframes.map { it.name }

                        filteredData = if(selectedTimeframes.isEmpty()){
                            data!!.filter { it.type in selectedTypes && it.city == selectedCity?.name }
                        } else{
                            //TODO probably should be done with API call(no call allowing list of timeframes and types for now)
                            data!!.filter {it.type in selectedTimeframes.map {t -> t.type } && it.city == selectedCity?.name}
                        }

                        fillDistances(false)
                        filteredData?.let { viewState.updatePlaces(it) }

                        viewState.showClear()
                    }

                } else {}
            }

            else -> {}
        }
    }

    fun setActualData(){
        actualDataLoaded = true
        fillDistances(true)
        data?.let { viewState.updatePlaces(it) }
    }

    private fun updateDistances() {
        launch {
            if(locationPoint != null){
                fillDistances(true)
                viewState.updateDistances()
            }
        }
    }

    private fun loadData() {
        launch {
            viewState.showProgress()

            cities = repository.getCities().await()

            selectedCity = cities!![0]
            viewState.changeCityName(selectedCity!!.name)

            data = repository.getPlacesByFilters(PlaceData().apply {
                selectedCity?.let {
                    city = selectedCity!!.name
                } }).await()

            updateDistances()

            timeframes = repository.getTimeFrames().await().toMutableList()
            types = repository.getPlaceTypes().await().map{ it.type }.filterNotNull().toMutableList()

            allFilters = types

            var calendar = Calendar.getInstance()

            for (x in 0 until 7) {
                days.add(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1).toUpperCase())
                actualDates.add(calendar.get(Calendar.DAY_OF_MONTH).toString()+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR))

                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            viewState.hideProgress()
            viewState.showData(data!!, allFilters, allSelected, days)

            initialized = true
        }
    }

    fun itemClicked(place: Place) {
        val id = place.id

        if(allSelected.isEmpty()){
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
            AnalyticsManager.logEvent(AnalyticsEvent(
                    AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = place.name },
                    hashMapOf("id" to id.toString())),
                    repository)

        } else{
            AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED.apply { venueName = place.name }, hashMapOf("id" to id.toString())), repository)
            AnalyticsManager.logEvent(AnalyticsEvent(
                    //TODO change to RESTAURANT_OPENED_USING_FILTERS ?
                    AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST.apply { venueName = place.name },
                    hashMapOf("id" to id.toString())),
                    repository)
        }

        router.navigateTo(SCREENS.PLACE, id)
    }

    private fun fillDistances(actualData: Boolean){
        locationPoint?.let {
            if(actualData) {
                data?.let {
                    if(!distancesFilled){
                        distancesFilled = true

                        data!!.forEach { place ->
                            val placePoint = place.location.latLng()

                            val distance = placePoint.distanceTo(locationPoint!!).toInt()

                            place.distance = distance
                        }
                        data = data!!.sortedBy { it.distance }
                    }
                }
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
