package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DriverRide(
        @Json(name="_id")
        var id: String = "",
        var drivers: List<String> = listOf(),
        var place: Long = 0,
        var timeframe: Timeframe? = null,
        var rides: List<String> = listOf(),
        var createdAt: String = ""

) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Timeframe(
            var start: String = "",
            var end: String = ""
    ) : Parcelable

}