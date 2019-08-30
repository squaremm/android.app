package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PlaceData(
        @Json(name="tf")
        var timeFrame: List<String> = listOf(),
        var typology: List<String> = listOf(),
        var date: String = ""
) : Parcelable