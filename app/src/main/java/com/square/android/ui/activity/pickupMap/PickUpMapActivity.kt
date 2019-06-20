package com.square.android.ui.activity.pickupMap

import android.location.Location
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.R
import com.square.android.data.pojo.CampaignLocationWrapper
import com.square.android.presentation.presenter.pickupMap.PickUpMapPresenter
import com.square.android.presentation.view.pickupMap.PickUpMapView
import com.square.android.ui.activity.BaseMapActivity
import com.square.android.ui.activity.campaignDetails.EXTRA_INTERVAL_SELECTED
import com.square.android.ui.activity.campaignDetails.EXTRA_LOCATIONS
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.activity_pick_up_map.*
import ru.terrakok.cicerone.Navigator

class PickUpMapExtras(val locationWrappers: List<CampaignLocationWrapper>, val selected: Long = 0)

//TODO: check if working now, if not -> change this ac to be full screen like in the project
class PickUpMapActivity: BaseMapActivity(), PickUpMapView, PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    //TODO if going back not working -> try ReviewActivity: provideNavigator() (activity opened from another activity(SelectOfferActivity))
    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun provideMapView() : com.mapbox.mapboxsdk.maps.MapView = map

    @InjectPresenter
    lateinit var presenter: PickUpMapPresenter

    @ProvidePresenter
    fun providePresenter() = PickUpMapPresenter(geLocationWrappers(), getSelected())

    private var previousMarker : Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_up_map)

        pickBack.setOnClickListener { presenter.back() }
    }

    override fun locate(location: LatLng) {
        centerOn(location)
    }

    override fun showInfo(locationWrappers: List<CampaignLocationWrapper>, selected: Long) {
        var selectedAdded = false

        val markerOptions = locationWrappers.map { locationWrapper ->

            val latLng = locationWrapper.location!!.latLng()

            val key = locationWrapper.intervalId.toString()

            if(locationWrapper.intervalId == selected){
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

    private fun geLocationWrappers() = intent.getParcelableArrayListExtra<CampaignLocationWrapper>(EXTRA_LOCATIONS) as List<CampaignLocationWrapper>

    private fun getSelected() = intent.getLongExtra(EXTRA_INTERVAL_SELECTED, 0)
}
