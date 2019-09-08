package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PlaceData(
        var timeFrame: String = "",
        var typology: String = "",
        var date: String = "",
        var city: String = ""
) : Parcelable