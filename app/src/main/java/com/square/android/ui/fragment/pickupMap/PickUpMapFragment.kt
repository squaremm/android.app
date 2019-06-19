package com.square.android.ui.fragment.pickupMap

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.pickupMap.PickUpMapPresenter
import com.square.android.presentation.view.pickupMap.PickUpMapView
import com.square.android.ui.activity.campaignDetails.EXTRA_INTERVALS
import com.square.android.ui.activity.campaignDetails.EXTRA_INTERVAL_SELECTED
import com.square.android.ui.fragment.BaseMapFragment
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_pick_up_map.*
import org.jetbrains.anko.bundleOf

class PickUpMapFragment: BaseMapFragment(), PickUpMapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(intervals: List<CampaignInterval>, selected: Long = 0): PickUpMapFragment {
            val fragment = PickUpMapFragment()

            val args = bundleOf(EXTRA_INTERVALS to intervals, EXTRA_INTERVAL_SELECTED to selected )
            fragment.arguments = args

            return fragment
        }
    }

    override fun provideMapView() : com.mapbox.mapboxsdk.maps.MapView = map

    @InjectPresenter
    lateinit var presenter: PickUpMapPresenter

    @ProvidePresenter
    fun providePresenter() = PickUpMapPresenter(geIntervals(), getSelected())

    private var previousMarker : Marker? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_up_map, container, false)
    }

    override fun locate(location: LatLng) {
        centerOn(location)
    }

    override fun showInfo(intervals: List<CampaignInterval>, selected: Long) {
        var selectedAdded = false

        val markerOptions = intervals.map { interval ->

            val latLng = interval.location!!.latLng()

            val key = interval.id.toString()

            if(interval.id == selected){
                selectedAdded = true

                MarkerOptions()
                        .title(key)
                        .position(latLng)
                        .icon(markerIconPink)
            } else{
                MarkerOptions()
                        .title(key)
                        .position(latLng)
                        .icon(markerIconGray)
            }
        }

        mapboxMap?.addMarkers(markerOptions)

        if(selectedAdded){
            previousMarker = mapboxMap?.markers!!.firstOrNull{it.title == selected.toString()}
        }
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

        pickMapMyLocation.setOnClickListener {
            presenter.locateClicked()
        }

        loadMapData()
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    private fun loadMapData() {
        presenter.loadData()
    }

    private fun geIntervals() = arguments?.getParcelableArrayList<CampaignInterval>(EXTRA_INTERVALS) as List<CampaignInterval>

    private fun getSelected() = arguments?.getLong(EXTRA_INTERVAL_SELECTED, 0) ?: 0
}
