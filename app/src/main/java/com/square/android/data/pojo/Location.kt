package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class Location(
        var coordinates: List<Double> = listOf(),
        var type: String = ""
) : Parcelable {
    fun latLng() : LatLng {
        val (lat, lon) = coordinates
        return LatLng(lat, lon)
    }
}