package com.square.android.data.pojo

import com.squareup.moshi.JsonClass
import com.mapbox.mapboxsdk.geometry.LatLng

@JsonClass(generateAdapter = true)
class Location(
        var coordinates: List<Double> = listOf(),
        var type: String = ""
) {
    fun latLng() : LatLng {
        val (lat, lon) = coordinates
        return LatLng(lat, lon)
    }
}