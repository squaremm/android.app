@file:Suppress("DEPRECATION")

package com.square.android.ui.fragment.aboutPlace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapView
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.presentation.presenter.aboutPlace.AboutPlacePresenter
import com.square.android.presentation.view.aboutPlace.AboutPlaceView
import com.square.android.ui.fragment.BaseMapFragment
import kotlinx.android.synthetic.main.fragment_about_place.*


private const val LOCATION_ZOOM_LEVEL = 15.0

class AboutPlaceFragment : BaseMapFragment(), AboutPlaceView {
    private var place: Place? = null

    override fun provideMapView(): MapView = aboutPlaceMap

    override fun isLocationEnabled() = false

    override fun mapReady() {
        mapboxMap!!.uiSettings.setAllGesturesEnabled(false)

        place?.let {
            showMap(it)
        }
    }

    @InjectPresenter
    lateinit var presenter: AboutPlacePresenter

    override fun showDistance(distance: Int) {
        aboutPlaceDistance.text = distance.asDistance()

        aboutPlaceDistance.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_place, container, false)
    }

    override fun showData(place: Place) {
        aboutPlaceDay.text = place.stringDays()
        aboutPlaceTime.text = place.stringTime()

        aboutPlaceDescription.text = place.description
        aboutPlaceAddress.text = place.address

        if (mapboxMap != null) {
            showMap(place)
        } else {
            this.place = place
        }
    }

    private fun showMap(place: Place) {
        val placeLocation = place.location.latLng()

        val cameraPosition = CameraPosition.Builder()
                .target(placeLocation)
                .zoom(LOCATION_ZOOM_LEVEL)
                .tilt(0.0)
                .bearing(0.0)
                .build()

        mapboxMap?.cameraPosition = cameraPosition

        val marker = MarkerOptions()
                .position(placeLocation)
                .icon(markerBackground)

        mapboxMap!!.addMarker(marker)
    }
}
