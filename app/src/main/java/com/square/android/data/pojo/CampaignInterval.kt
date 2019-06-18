package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class CampaignInterval(
        @field:JsonProperty("_id")
        var id: String? = null,
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
    ) : Parcelable

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