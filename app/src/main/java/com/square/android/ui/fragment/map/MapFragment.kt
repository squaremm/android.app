@file:Suppress("DEPRECATION")

package com.square.android.ui.fragment.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.presentation.presenter.map.MapPresenter
import com.square.android.presentation.view.map.MapView
import com.square.android.ui.fragment.BaseMapFragment
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.place_map.view.*


class MapFragment : BaseMapFragment(), MapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {
    @InjectPresenter
    lateinit var presenter: MapPresenter

    private var previousMarker : Marker? = null

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

            previousMarker?.let { it.icon = markerIconGray }

            it.icon = markerIconPink

            previousMarker = it
            true
        }

        mapboxMap!!.addOnMapClickListener {
            presenter.mapClicked()
            true
        }

        mapMyLocation.setOnClickListener {
            presenter.locateClicked()
        }

        mapPlaceInfo.setOnClickListener { presenter.infoClicked() }

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
                    .icon(markerIconGray)
        }

        mapboxMap?.addMarkers(markerOptions)

        mapPlaceInfo.mapPlaceRv.layoutManager = LinearLayoutManager(mapPlaceInfo.mapPlaceRv.context,RecyclerView.HORIZONTAL,false)
        mapPlaceInfo.mapPlaceRv.adapter = MapPlaceImagesAdapter(listOf())
        mapPlaceInfo.mapPlaceRv.addItemDecoration(MarginItemDecorator( mapPlaceInfo.mapPlaceRv.context.resources.getDimension(R.dimen.rv_item_decorator_width).toInt()))
    }

    override fun showInfo(place: Place) {
        updateCurrentInfoDistance(place.distance)

        mapPlaceInfo.mapPlaceTitle.text = place.name
        mapPlaceInfo.mapPlaceAddress.text = place.address

        mapPlaceInfo.visibility = View.VISIBLE

        mapPlaceInfo.mapPlaceTypeLabel.text = place.type[0]

        //TODO change mapPlaceInfo.mapPlaceTypeIcon for different types of places

        place.photos?.let {(mapPlaceInfo.mapPlaceRv.adapter as MapPlaceImagesAdapter).setUrls(it)}
                ?: (mapPlaceInfo.mapPlaceRv.adapter as MapPlaceImagesAdapter).setUrls(listOf())

        if((mapPlaceInfo.mapPlaceRv.adapter as MapPlaceImagesAdapter).imageUrls.isEmpty()){
            mapPlaceInfo.mapPlaceRv.visibility = View.GONE
        } else  mapPlaceInfo.mapPlaceRv.visibility = View.VISIBLE

    }

    override fun updateCurrentInfoDistance(distance: Int?) {
        if (distance != null) {
            mapPlaceInfo.mapPlaceDistance.text = distance.asDistance()
            mapPlaceInfo.mapPlaceDistance.visibility = View.VISIBLE
        } else {
            mapPlaceInfo.mapPlaceDistance.visibility = View.GONE
        }
    }

    override fun hideInfo() {
        mapPlaceInfo.visibility = View.GONE

        previousMarker?.let { it.icon = markerIconGray }
    }

    private fun loadMapData() {
        presenter.loadData()
    }
}
