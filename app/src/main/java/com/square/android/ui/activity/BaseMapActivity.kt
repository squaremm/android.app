package com.square.android.ui.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.square.android.R
import com.square.android.extensions.getBitmap

private const val LOCATION_ZOOM_LEVEL = 14.0
private const val LOCATION_ZOOM_ANIMATION = 3000L

abstract class BaseMapActivity: LocationActivity(){
    protected lateinit var mapView: MapView

    protected var mapboxMap: MapboxMap? = null

    protected val markerBackground by lazy { getDefaultMarkerBackground() }

    protected val markerIconGray by lazy { getGrayMarker() }

    protected val markerIconPink by lazy { getPinkMarker() }

    private lateinit var style: Style

    protected var locationComponent: LocationComponent? = null

    private var isLocationAllowed = false

    override fun locationGotten(lastLocation: Location?) {}

    abstract fun provideMapView(): MapView

    abstract fun mapReady()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = provideMapView()
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { map ->
            mapboxMap = map

            map.setStyle(Style.LIGHT) {
                style = it

                initMap()
            }
        }
    }

    private fun initMap() {
        if (isLocationAllowed) initMapLocation()

        mapboxMap!!.uiSettings.isLogoEnabled = false
        mapboxMap!!.uiSettings.isAttributionEnabled = false

        mapReady()
    }

    override fun locationAllowed() {
        isLocationAllowed = true

        if (mapboxMap != null) {
            initMapLocation()
        }
    }

    private fun getDefaultMarkerBackground(): Icon {
        val bitmap = getBitmap(R.drawable.marker_background)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    private fun getGrayMarker(): Icon {
        val bitmap = getBitmap(R.drawable.ic_marker_gray)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    private fun getPinkMarker(): Icon {
        val bitmap = getBitmap(R.drawable.ic_marker_pink)

        val iconFactory = IconFactory.getInstance(this)

        return iconFactory.fromBitmap(bitmap)
    }

    @SuppressLint("MissingPermission")
    private fun initMapLocation() {
        val options = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.nice_pink))
                .accuracyAlpha(0.08f)
                .bearingTintColor(android.R.color.transparent)
                .elevation(0f)
                .foregroundDrawable(R.drawable.my_location)
                .foregroundDrawableStale(R.drawable.my_location)
                .backgroundDrawable(R.drawable.my_location_bg)
                .backgroundDrawableStale(R.drawable.my_location_bg)
                .build()

        locationComponent = mapboxMap!!.locationComponent

        val nonNullComponent = locationComponent!!

        nonNullComponent.activateLocationComponent(this, style, options)

        nonNullComponent.isLocationComponentEnabled = true

        nonNullComponent.cameraMode = CameraMode.TRACKING
        nonNullComponent.renderMode = RenderMode.COMPASS
        nonNullComponent.zoomWhileTracking(LOCATION_ZOOM_LEVEL, LOCATION_ZOOM_ANIMATION)
    }

    protected fun centerOn(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
                .target(location)
                .zoom(LOCATION_ZOOM_LEVEL)
                .tilt(0.0)
                .bearing(0.0)
                .build()

        val update = CameraUpdateFactory.newCameraPosition(cameraPosition)

        mapboxMap?.animateCamera(update, LOCATION_ZOOM_ANIMATION)
    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()

        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()

        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()

        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.visibility = View.GONE
        removeFromSuperview(mapView)
        findViewById<ViewGroup>(R.id.container)?.addView(mapView)
        mapView.onDestroy()
        removeFromSuperview(mapView)
    }

    fun removeFromSuperview(view: View) {
        val parent = view.parent
        if (parent != null && parent is ViewGroup) {
            parent.removeView(view)
        }
    }
}

private fun MapboxMap.animateCamera(update: CameraUpdate, animationDuration: Long) {
    animateCamera(update, animationDuration.toInt())
}