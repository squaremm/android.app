package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class IntervalsWrapper(
        @field:JsonProperty("_id")
        val id: String = "",

        val intervals: List<Place.Interval> = listOf(),

        @field:JsonProperty("place")
        val placeId: Int = 0,

        val message: String? = null
)