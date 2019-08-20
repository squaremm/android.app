package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class PlaceType(

        @Json(name="_id")
        var id: String = "",
        var type: String? = null,
        var image: String = ""

) : Parcelable
