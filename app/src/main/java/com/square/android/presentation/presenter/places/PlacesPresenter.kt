package com.square.android.presentation.presenter.places

import android.location.Location
import com.arellomobile.mvp.InjectViewState
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
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

    private var locationPoint: LatLng? = null

    private var data: List<Place>? = null

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

            viewState.updateDistances()
        }
    }

    private fun loadData() {
        launch {
            data = repository.getPlaces().await()

            if (locationPoint != null) fillDistances().await()


            viewState.hideProgress()
            viewState.showPlaces(data!!)
        }
    }

    fun itemClicked(position: Int) {
        val id = data!![position].id

        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.VENUE_CLICKED, hashMapOf("id" to id.toString())))
        AnalyticsManager.logEvent(AnalyticsEvent(AnalyticsEvents.RESTAURANT_OPENED_FROM_LIST, hashMapOf("id" to id.toString())))

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
