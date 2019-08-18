package com.square.android.presentation.presenter.place

import android.location.Location
import android.text.TextUtils
import com.arellomobile.mvp.InjectViewState
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.BookInfo
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.extensions.getStringDate
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.main.BadgeStateChangedEvent
import com.square.android.presentation.presenter.redemptions.RedemptionsUpdatedEvent
import com.square.android.presentation.view.place.PlaceView
import com.square.android.utils.AnalyticsEvent
import com.square.android.utils.AnalyticsEvents
import com.square.android.utils.AnalyticsManager
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject
import java.lang.Exception
import java.util.*

@InjectViewState
class PlacePresenter(private val placeId: Long) : BasePresenter<PlaceView>() {
    private var locationPoint: LatLng? = null

    private val eventBus: EventBus by inject()

    private var data: Place? = null

    private var offers: List<OfferInfo> = listOf()

    private var currentPositionOffers = 0

    private var currentPosition: Int? = null

    private var calendar: Calendar = Calendar.getInstance()
    private var calendar2: Calendar = Calendar.getInstance()

    private lateinit var intervalSlots: List<Place.Interval>

    init {
        loadData()
    }

    fun itemClicked(position: Int) {
        viewState.setSelectedItem(currentPosition, position)

        currentPosition = position
    }

    fun bookClicked() {
        currentPosition?.let {
            launch {
                val date = getStringDate()
                val userId = repository.getUserInfo().id
                val bookInfo = BookInfo(userId, date, intervalSlots[it].id)
                val result = repository.book(placeId, bookInfo).await()

                val redemptionsEvent = RedemptionsUpdatedEvent()
                val badgeEvent = BadgeStateChangedEvent()

                loadIntervals()

                eventBus.post(redemptionsEvent)
                eventBus.post(badgeEvent)

                AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.BOOKING_MADE.apply { venueName = data?.name }), repository)

                if(!TextUtils.isEmpty(result.message)){
                    viewState.showMessage(result.message)
                }
            }
        }
    }

    fun dayItemClicked(position: Int) {
        viewState.setSelectedDayItem(position)

        calendar2.timeInMillis = calendar.timeInMillis
        calendar2.add(Calendar.DAY_OF_YEAR, position)

        viewState.updateMonthName(calendar2)

        loadIntervals()
    }








    private fun loadIntervals() {
        launch {
            viewState.showProgress()

//            val intervalsWrapper = repository.getIntervals(place?.id!!, getStringDate()).await()
            intervalSlots = repository.getIntervalSlots(data!!.id, getStringDate()).await()

            viewState.hideProgress()

            viewState.showIntervals(intervalSlots)
        }
    }







    fun getStringDate() = calendar2.getStringDate()

    fun locationGotten(lastLocation: Location?) {
        lastLocation?.let {
            locationPoint = LatLng(it.latitude, it.longitude)

            if (data != null) {
                updateLocationInfo()
            }
        }
    }

    private fun loadData() {
        launch {
            data = repository.getPlace(placeId).await()

            offers = data!!.offers

            viewState.showData(data!!, offers!!,calendar)

            if (locationPoint != null) {
                updateLocationInfo()
            }
        }
    }

    fun offersItemClicked(position: Int, place: Place?) {
        try{
            currentPositionOffers = position
            viewState.setSelectedOfferItem(position)

            val offer = offers!![currentPositionOffers]

            viewState.showOfferDialog(offer, place)

        } catch (e: Exception){

        }
    }

    private fun updateLocationInfo() {
        val placePoint = data!!.location.latLng()

        val distance = placePoint.distanceTo(locationPoint!!).toInt()

        data!!.distance = distance

        showLocationInfo()
    }

    private fun showLocationInfo() {
        viewState.showDistance(data!!.distance)
    }
}