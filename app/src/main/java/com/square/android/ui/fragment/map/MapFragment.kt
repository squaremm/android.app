@file:Suppress("DEPRECATION")

package com.square.android.ui.fragment.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadFirstOrPlaceholder
import com.square.android.presentation.presenter.map.MapPresenter
import com.square.android.presentation.view.map.MapView
import com.square.android.ui.fragment.BaseMapFragment
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.place_card.view.*


class MapFragment : BaseMapFragment(), MapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    @InjectPresenter
    lateinit var presenter: MapPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun provideMapView() : com.mapbox.mapboxsdk.maps.MapView = map

    override fun locate(location: LatLng) {
        centerOn(location)
    }

    override fun mapReady() {
        mapboxMap?.setOnMarkerClickListener {
            val markerId = it.title.toLong()

            presenter.markerClicked(markerId)


            true
        }

        mapboxMap!!.addOnMapClickListener {
            presenter.mapClicked()
            true
        }

        mapMyLocation.setOnClickListener {
            presenter.locateClicked()
        }

        placeInfo.setOnClickListener { presenter.infoClicked() }

        loadMapData()
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showPlaces(data: List<Place>) {
        val markerOptions = data.map { place ->
            val latLng = place.location.latLng()

            val key = place.id.toString()

            MarkerOptions()
                    .title(key)
                    .position(latLng)
                    .icon(markerBackground)
        }


        mapboxMap?.addMarkers(markerOptions)
    }

    override fun showInfo(place: Place) {
        updateCurrentInfoDistance(place.distance)

        placeInfo.placeInfoImage.loadFirstOrPlaceholder(place.photos)

        placeInfo.placeInfoCredits.text = place.award.toString()
        placeInfo.placeInfoTitle.text = place.name
        placeInfo.placeInfoAddress.text = place.address

        placeInfo.visibility = View.VISIBLE
    }

    override fun updateCurrentInfoDistance(distance: Int?) {
        if (distance != null) {
            val distanceFormatted = getString(R.string.distance_format, distance.asDistance())

            placeInfo.placeInfoDistance.text = distanceFormatted
            placeInfo.placeInfoDistance.visibility = View.VISIBLE
        } else {
            placeInfo.placeInfoDistance.visibility = View.GONE
        }
    }

    override fun hideInfo() {
        placeInfo.visibility = View.GONE
    }

    private fun loadMapData() {
        presenter.loadData()
    }
}
