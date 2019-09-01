package com.square.android.data.pojo

import android.os.Parcelable
import com.square.android.data.network.IgnoreObjectIfIncorrect
import com.square.android.data.network.IgnoreStringForArrays
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
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
        @Transient
        var schedule: Map<String, ScheduleDay> = mapOf(),
        var type: String = "",

        var icons: Icons? = null,



        //TODO data for Party
        @Transient
        var placesOffers: List<PlaceOffers> = listOf(),
        @Transient
        var timeframe: Timeframe? = null,
        @Transient
        var participants: List<Long> = listOf(),
        @Transient
        var requirements: Map<String, String> = mapOf(),




        //TODO this stays here, set this to slots from PlaceOffers when making a list of Place in party
        @Transient
        var slots: Int = 0,



        // Availability label data
        var availableOfferDay: String? = null,
        var availableOfferSpots: Int = 0

) : Parcelable {


    @Parcelize
    @JsonClass(generateAdapter = true)
    class Icons(
            var typology: List<String> = listOf(),
            var extras: List<String> = listOf()
    ) : Parcelable




    //TODO data for Party
    @Parcelize
    @JsonClass(generateAdapter = true)
    class Timeframe(
            var spots: Int = 0,
            var start: String = "",
            var end: String = "",
            var description: String = ""
    ) : Parcelable
    @Parcelize
    @JsonClass(generateAdapter = true)
    class PlaceOffers(
            var slots: Int = 0,
            var placeId: Long = 0,
            var offerIds: List<Long> = listOf()
    ) : Parcelable





    var distance: Int? = null

    var award: Int = 0

    fun stringDays() = schedule.keys
            .filterNot { it.isEmpty() }
            .joinToString(separator = "\n", transform = String::capitalize)

    fun stringTime() = schedule.values
            .map { it.start + " " + it.end }
            .joinToString(separator = "\n")


    @Parcelize
    @JsonClass(generateAdapter = true)
    class Interval(
            @Json(name="_id")
            var id: String? = null,
            var start: String = "",
            var end: String = "",
            var offers: List<Long> = listOf(),
            @Json(name="free")
            var slots: Int = 0,
            var description: String = ""
    ) : Parcelable

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


