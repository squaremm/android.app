package com.square.android.data.pojo

import com.square.android.data.network.IgnoreObjectIfIncorrect
import com.square.android.data.network.IgnoreStringForArrays
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
class Place(
        @Json(name="_id")
        var id: Long = 0,
        var address: String = "",
//        var bookings: List<Booking> = listOf(),
        var intervals: List<Interval> = listOf(),
        var credits: Int = 0,
        var description: String = "",
        var level: Int? = 0,
        var location: Location = Location(),
        var name: String = "",
        var offers: List<OfferInfo> = listOf(),
        var photos: List<String>? = listOf(),
        var mainImage: String? = null,
        @IgnoreObjectIfIncorrect.IgnoreJsonObjectError
        var schedule: Map<String, ScheduleDay> = mapOf(),
        var type: String = "",

        // Availability label data
        var availableOfferDay: String? = null,
        var availableOfferSpots: Int = 0

) {
    var distance: Int? = null

    var award: Int = 0

    fun stringDays() = schedule.keys
            .filterNot { it.isEmpty() }
            .joinToString(separator = "\n", transform = String::capitalize)

    fun stringTime() = schedule.values
            .map { it.start + " " + it.end }
            .joinToString(separator = "\n")


    @JsonClass(generateAdapter = true)
    class Interval(
            @Json(name="_id")
            var id: String? = null,

            var start: String = "",
            var end: String = "",

            @Json(name="free")
            var slots: Int = 0
    )

    data class Booking(
            @Json(name="_id")
            var id: Int = 0,
            var closed: Boolean = false,
            var date: String = "",
            var endTime: String = "",
            var name: String = "",
            var place: Int = 0,
            var startTime: String = "",
            var user: Int = 0
    )
}


