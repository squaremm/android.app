package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class BookEventData(
        var eventId: String = "",
        var bookings: List<EventBooking> = listOf()
) : Parcelable{

    @Parcelize
    @JsonClass(generateAdapter = true)
    class EventBooking(
           var placeId: Long? = null,
           var intervalId: String = "",
           var date: String = "",
           var offerIds: List<Long> = listOf()
    ) : Parcelable

}