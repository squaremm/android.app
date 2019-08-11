package com.square.android.data.pojo

import com.squareup.moshi.Json

data class IntervalsWrapper(
        @Json(name="_id")
        val id: String = "",

        val intervals: List<Place.Interval> = listOf(),

        @Json(name="place")
        val placeId: Int = 0,

        val message: String? = null
)