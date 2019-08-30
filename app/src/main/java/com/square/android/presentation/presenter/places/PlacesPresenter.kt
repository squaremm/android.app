package com.square.android.presentation.presenter.places

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.data.pojo.FilterTimeframe
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceData
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.places.PlacesView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.Exception
import java.util.*

@InjectViewState
class PlacesPresenter : BasePresenter<PlacesView>() {

    var filteringMode = 1

    var actualDataLoaded = true

    var shouldShowClear = false

    private var selectedDayPosition: Int? = null

    var types: MutableList<String> = mutableListOf()

    var days: MutableList<String> = mutableListOf()
    var actualDates: MutableList<String> = mutableListOf()

    var timeframes: MutableList<FilterTimeframe> = mutableListOf()

    var allFilters: MutableList<String> = mutableListOf()
    var allSelected: MutableList<String> = mutableListOf()

    private var locationPoint: LatLng? = null

    //TODO change to null
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
            val isTimeframe = timeframes.firstOrNull{it.name == allFilters[position]} != null

            if(isTimeframe){
                if(allFilters[position] in allSelected){
                    allSelected.remove(allFilters[position])
                } else{
                    allSelected.add(allFilters[position])
                }

                viewState.setSelectedFilterItems(listOf(position))
            } else{
                val contains = allSelected.contains(allFilters[position])
                val currentTimeframes = timeframes.filter { it.type == allFilters[position] }.map { it.name }

                if(contains){
                    allSelected.remove(allFilters[position])
                    if(currentTimeframes.isNotEmpty()){

                        //TODO ERROR when unchecking type in adapter when it's timeframe is checked (Select Restaurant and aperitif for example):
                        //     java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{6985864 position=7 id=-1, oldPos=-1, pLpos:-1 no parent}
                        //     androidx.recyclerview.widget.RecyclerView{ae067a4 VFED..... ......ID 334,170-1080,288 #7f0802f3 app:id/placesFiltersTypesRv},
                        //     adapter:com.square.android.ui.fragment.places.FiltersAdapter@aa7cd32,
                        //     layout:androidx.recyclerview.widget.LinearLayoutManager@a3ca83, context:com.square.android.ui.activity.main.MainActivity@c86029a

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

                viewState.updateFilters(allSelected)
            }

            checkFilters()

        } catch (e: Exception){

        }
    }

    fun dayClicked(position: Int){
        selectedDayPosition = position
        viewState.setSelectedDayItem(selectedDayPosition!!)
        checkFilters()
    }

    fun searchTextChanged(text: CharSequence?){
        searchText = text
        checkFilters()
    }

    fun refreshViewsForTypes(){
        val selectedIndexes: MutableList<Int> = mutableListOf()

        for(item in allSelected){
            selectedIndexes.add(allFilters.indexOf(item))
        }

        viewState.setSelectedFilterItems(selectedIndexes)
    }

    fun refreshViewsForDays(){
        selectedDayPosition?.let {
            viewState.setSelectedDayItem(it)
        }
    }

    //mode: 1 - search, 2 - date, 3 - types
    fun changeFiltering(mode: Int){
        filteringMode = mode

        checkFilters()
    }

    fun clearFilters(){
        viewState.hideClear()

        //TODO not removing timeframes

        allSelected = mutableListOf()
        allFilters = types

        viewState.updateFilters(allSelected)

        setActualData()
    }

    //filteringMode: 1 - search, 2 - date, 3 - types
    private fun checkFilters() = GlobalScope.async {
        when(filteringMode){
            1 -> {
                if (TextUtils.isEmpty(searchText)) {
                    setActualData()
                } else {
                    if(data != null){
                        actualDataLoaded = false

                        filteredData = data!!.filter { it.name.contains(searchText.toString(), true) }

                        fillDistances(false).await()
                        filteredData?.let { viewState.updatePlaces(it) }
                    } else {}
                }
            }

            2 -> {
                selectedDayPosition?.let {
                    actualDataLoaded = false

                    var mDate = actualDates[selectedDayPosition!!]

                    filteredData = repository.getPlacesByFilters(PlaceData().apply { date  = mDate }).await()

                    fillDistances(false).await()
                    filteredData?.let { viewState.updatePlaces(it) }
                } ?: run {
                    setActualData()
                }
            }

            3 -> {
                if(data != null){
                    if(allSelected.isEmpty()){
                        setActualData()
                        viewState.hideClear()
                        shouldShowClear = false
                    } else {
                        actualDataLoaded = false

                        val selectedTimeframes = timeframes.filter { it.name in allSelected }
                        val selectedTypes = allSelected - selectedTimeframes.map { it.name }

                        filteredData = if(selectedTimeframes.isEmpty()){
                            data!!.filter { it.type in selectedTypes }
                        } else{
                            data!!.filter {it.type in selectedTimeframes.map {t -> t.type } }
                        }

                        fillDistances(false).await()
                        filteredData?.let { viewState.updatePlaces(it) }

                        viewState.showClear()
                        shouldShowClear = true
                    }

                } else {}
            }

            else -> {}
        }
    }

    fun setActualData() = GlobalScope.async {
        actualDataLoaded = true
        fillDistances(true).await()
        data?.let { viewState.updatePlaces(it) }
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
//            updateDistances()

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
