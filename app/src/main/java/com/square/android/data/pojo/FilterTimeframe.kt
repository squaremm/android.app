package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class FilterTimeframe(
   @Json(name="_id")
   var id: String,
   var name: String = "",
   var type: String = ""
) : Parcelable