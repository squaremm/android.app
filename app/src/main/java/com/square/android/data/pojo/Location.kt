package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.mapbox.mapboxsdk.geometry.LatLng

@JsonIgnoreProperties(ignoreUnknown = true)
class Location(
        var coordinates: List<Double> = listOf(),
        var type: String = ""
) {
    fun latLng() : LatLng {
        val (lat, lon) = coordinates
        return LatLng(lat, lon)
    }
}