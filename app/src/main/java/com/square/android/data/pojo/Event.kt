package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class Event(
        @Json(name="_id")
        var id: String = "",
        var placeId: Long = 0,
        var requirements: List<Requirement> = listOf(),
        var placesOffers: List<PlaceOffers> = listOf(),
        var timeframe: EventTimeframe? = null,
        var participants: List<Long> = listOf(),
        var createdAt: String = "",
        var baseCredits: Int = 0,
        var level: Int? = null
) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Requirement(
            @Json(name="_id")
            var id: String = "",
            var name: String = "",
            var image: String = "",
            var value: String = ""
    ) : Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class PlaceOffers(
           var placeId: Long = 0,
           var offerIds: List<Long> = listOf(),


           //TODO need slots in PlaceOffers
           @Transient
           var slots: Int = 0
    ) : Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    class EventTimeframe(
            var start: String = "",
            var end: String = "",
            var spots: Int = 0,
            var freeSpots: Int = 0,
            var intervalId: String = ""
    ) : Parcelable
}
