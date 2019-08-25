package com.square.android.presentation.presenter.partyDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceType
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.party.PartyBookEvent
import com.square.android.presentation.presenter.party.SelectedPlaceEvent
import com.square.android.presentation.view.partyDetails.PartyDetailsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject
import java.util.*

@InjectViewState
class PartyDetailsPresenter(var party: Place) : BasePresenter<PartyDetailsView>() {

    private var offers: List<OfferInfo> = listOf()

    private var places: List<Place> = listOf()

    private var currentPositionPlaces: Int? = null
    private var currentPositionIntervals: Int? = null

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar = Calendar.getInstance()

    private lateinit var intervalSlots: List<Place.Interval>

    private val eventBus: EventBus by inject()

    //TODO event from PartyPlaceFragment when select clicked
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectedPlaceEvent(event: SelectedPlaceEvent) {
        event.data?.let {
            appendPlace(it.placeId)

            viewState.updateRestaurantName(it.placeName)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPartyBookEvent(event: PartyBookEvent) {
        doBooking()
    }

    init {
        eventBus.register(this)
        loadData()
    }

    private fun loadData() {
        launch {

            val placeTypes: List<PlaceType> = repository.getPlaceTypes().await()

            val placeImage: String? = placeTypes.filter { it.type == party.type}.firstOrNull()?.image

            viewState.showData(party, offers,calendar, placeImage)

            loadIntervals()
        }
    }

    fun intervalItemClicked(position: Int){
        currentPositionIntervals = position

        viewState.setSelectedIntervalItem(position)
    }

    private fun doBooking(){
//        val result = repository.bookParty(partyId, bookInfo).await()
//
//
//        val redemptionsEvent = RedemptionsUpdatedEvent()
//        val badgeEvent = BadgeStateChangedEvent()
//
//        eventBus.post(redemptionsEvent)
//        eventBus.post(badgeEvent)
//
//        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.PARTY_BOOKING_MADE.apply { venueName = data?.name }), repository)
//
//        if(!TextUtils.isEmpty(result.message)){
//            viewState.showMessage(result.message)
//        }

        viewState.hideBookingProgress()
    }

    private fun loadIntervals() {
        launch {
            viewState.showProgress()

            intervalSlots = repository.getIntervalSlots(party.id, getStringDate()).await()

            viewState.hideProgress()

            viewState.showIntervals(intervalSlots)
        }
    }

    fun dayItemClicked(position: Int) {
        viewState.setSelectedDayItem(position)

        calendar2.timeInMillis = calendar.timeInMillis
        calendar2.add(Calendar.DAY_OF_YEAR, position)

        viewState.updateMonthName(calendar2)

        loadIntervals()
    }

    fun getStringDate() = calendar2.getStringDate()

    private fun appendPlace(placeId: Long) {
        val place = places.filter { it.id == placeId}.firstOrNull()
        var index: Int?

        place?.let {
           index =  places.indexOf(place)

            currentPositionPlaces = index
            viewState.setSelectedPlaceItem(currentPositionPlaces!!)
        }

    }

    fun placeItemClicked(index: Int) {
        val placeId = places[index].id

        //TODO navigate to partyPlace
    }

    override fun onDestroy() {
        eventBus.unregister(this)
    }

}