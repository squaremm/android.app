//@file:Suppress("DEPRECATION")

package com.square.android.ui.fragment.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadFirstOrPlaceholder
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.map.MapPresenter
import com.square.android.presentation.view.map.MapView
import com.square.android.ui.fragment.BaseMapFragment
import com.square.android.ui.fragment.placesList.PlaceExtrasAdapter
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.place_map.view.*

class MapFragment(var data: MutableList<Place>) : BaseMapFragment(), MapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    @InjectPresenter
    lateinit var presenter: MapPresenter

    @ProvidePresenter
    fun providePresenter() = MapPresenter(data)

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
    }

    override fun updatePlaces(data: List<Place>) {
        previousMarker = null
        mapboxMap?.clear()

        val markerOptions = data.map { place ->
            val latLng = place.location.latLng()

            val key = place.id.toString()

            MarkerOptions()
                    .title(key)
                    .position(latLng)
                    .icon(markerIconGray)
        }

        mapboxMap?.addMarkers(markerOptions)
    }

    override fun showInfo(place: Place) {
        updateCurrentInfoDistance(place.distance)

        mapPlaceInfo.mapPlaceAvailableValue.text = if(place.availableOfferSpots > 0) place.availableOfferSpots.toString() else mapPlaceInfo.mapPlaceAvailableValue.context.getString(R.string.no)
        mapPlaceInfo.mapPlaceTitle.text = place.name
        mapPlaceInfo.mapPlaceAddress.text = place.address

        mapPlaceInfo.visibility = View.VISIBLE

        place.icons?.let {
            mapPlaceInfo.mapPlaceExtrasRv.visibility = View.VISIBLE
            mapPlaceInfo.mapPlaceExtrasRv.adapter = PlaceExtrasAdapter(it.extras)
            mapPlaceInfo.mapPlaceExtrasRv.layoutManager = LinearLayoutManager(mapPlaceInfo.mapPlaceExtrasRv.context, RecyclerView.HORIZONTAL,false)
            mapPlaceInfo.mapPlaceExtrasRv.addItemDecoration(MarginItemDecorator(mapPlaceInfo.mapPlaceExtrasRv.context.resources.getDimension(R.dimen.rv_item_decorator_minus_1).toInt(), false))
        }

        if (place.mainImage != null) {
            mapPlaceInfo.mapPlaceInfoImage.loadImage(place.mainImage!!, R.color.placeholder)
        } else {
            mapPlaceInfo.mapPlaceInfoImage.loadFirstOrPlaceholder(place.photos)
        }
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
