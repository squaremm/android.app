package com.square.android.data.pojo

import com.square.android.data.network.IgnoreObjectIfIncorrect
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Party(
        @Json(name="_id")
        var id: Long = 0,
        var address: String = "",
//        var bookings: List<Booking> = listOf(),
        var intervals: List<Place.Interval> = listOf(),
        var credits: Int = 0,
        var description: String = "",
        var level: Int? = 0,
        var location: Location = Location(),
        var name: String = "",
        var offers: List<OfferInfo> = listOf(),
        var photos: List<String>? = listOf(),
        var mainImage: String? = null,
        @IgnoreObjectIfIncorrect.IgnoreJsonObjectError
        @Transient
        var schedule: Map<String, ScheduleDay> = mapOf(),
        var type: String = "",

        var extra: List<String> = listOf()
) {
    var distance: Int? = null

    var award: Int = 0

    fun stringDays() = schedule.keys
            .filterNot { it.isEmpty() }
            .joinToString(separator = "\n", transform = String::capitalize)

    fun stringTime() = schedule.values
            .map { it.start + " " + it.end }
            .joinToString(separator = "\n")
}