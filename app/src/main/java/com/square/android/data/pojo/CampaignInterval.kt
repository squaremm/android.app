package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class CampaignInterval(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var location: Location? = null,
        var slots: List<Slot>? = null
): Parcelable {

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Location(
            @field:JsonProperty("_id")
            var id: String? = null,

            var address: String? = null,
            var city: String? = null,
            var coordinates: List<Double> = listOf()
    ) : Parcelable{
        fun getAddressString(): String{
            return "$address, $city"
        }

        fun latLng() : LatLng {
            val (lat, lon) = coordinates
            return LatLng(lat, lon)
        }
    }

    @Parcelize
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Slot(
            @field:JsonProperty("_id")
            var id: String? = null,

            var start: String = "",
            var end: String = "",
            var day: String? = null,
            @field:JsonProperty("free")
            var slots: Int = 0,

            var date: String? = null,
            var startTime: String? = null

    ) : Parcelable
}