package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Ride(
        @Json(name="_id")
        var id: String = "",
        var driverRideId: String = "",
        var from: RideData.LngLat? = null,
        var to: RideData.LngLat? = null,
        var fromPlace: Long? = 0,
        var toPlace: Long? = 0,
        var eventBookingId: String = "",
        var address: String = ""

) : Parcelable