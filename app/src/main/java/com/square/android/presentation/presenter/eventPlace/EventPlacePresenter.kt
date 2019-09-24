package com.square.android.presentation.presenter.eventPlace

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.event.SelectedPlaceEvent
import com.square.android.presentation.presenter.event.SelectedPlaceExtras
import com.square.android.presentation.view.eventPlace.EventPlaceView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject
import java.lang.Exception

@InjectViewState
class EventPlacePresenter(var place: Place) : BasePresenter<EventPlaceView>() {

    private var currentPositionIntervals: Int? = null

    private var offers: List<OfferInfo> = listOf()

    private lateinit var intervalSlots: List<Place.Interval>

    private var offersSelectedPositions: MutableList<Int> = mutableListOf()

    private val eventBus: EventBus by inject()

    init {
        offers = place.offers

        viewState.showData(place, offers)

        loadIntervals()
    }

    fun intervalItemClicked(position: Int){
        currentPositionIntervals = position

        viewState.setSelectedIntervalItem(position)
    }

    fun selectClicked(){
        eventBus.post(SelectedPlaceEvent(SelectedPlaceExtras(place.id,place.name)))

        router.backTo(SCREENS.EVENT_DETAILS)
    }

    fun offersItemLongClicked(position: Int, place: Place?) {
        try{
            val offer = offers[position]

            viewState.showOfferDialog(offer, place)

        } catch (e: Exception){

        }
    }

    fun offersItemClicked(position: Int){
        if(position in offersSelectedPositions){
            offersSelectedPositions.remove(position)
        } else{
            offersSelectedPositions.add(position)
        }
    }

    private fun loadIntervals() {
        launch {
            //TODO date will be defined?
//            intervalSlots = repository.getIntervalSlots(place.id, getStringDate()).await()
//
//            viewState.showIntervals(intervalSlots)
        }
    }
}