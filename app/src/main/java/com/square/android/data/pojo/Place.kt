package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Place(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var address: String = "",
//        var bookings: List<Booking> = listOf(),
        var intervals: List<Interval> = listOf(),
        var credits: Int = 0,
        var description: String = "",
        var level: Int = 0,
        var location: Location = Location(),
        var name: String = "",
        var offers: List<OfferInfo> = listOf(),
        var photos: List<String>? = listOf(),
        var mainImage: String? = null,
        var schedule: Map<String, String> = mapOf(),
        var type: String = "",

        // Availability label data
        var availableOfferDay: String? = null,
        var availableOfferName: String? = null,
        var availableOfferSpots: Int = 0

) {
    var distance: Int? = null

    var award: Int = 0

    fun stringDays() = schedule.keys
            .filterNot { schedule[it].isNullOrEmpty() }
            .joinToString(separator = "\n", transform = String::capitalize)

    fun stringTime() = schedule.values
            .filter(String::isNotEmpty)
            .joinToString(separator = "\n")


    @JsonIgnoreProperties(ignoreUnknown = true)
    class Interval(
            @field:JsonProperty("_id")
            var id: String? = null,

            var start: String = "",
            var end: String = "",

            @field:JsonProperty("free")
            var slots: Int = 0
    )

    data class Booking(
            @JsonProperty("_id")
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


