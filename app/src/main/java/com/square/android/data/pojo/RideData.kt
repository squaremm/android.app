package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RideData(
        var driverRideId: String = "",
        var from: LngLat? = null,
        var to: LngLat? = null,
        var fromPlace: Long? = 0,
        var toPlace: Long? = 0,
        var eventBookingId: String = "",
        var address: String = ""

) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class LngLat(
            var longitude: Double? = null,
            var latitude: Double? = null
    ) : Parcelable

}