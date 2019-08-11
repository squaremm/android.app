package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
        var id: String = "",

        var url: String = "",

        var cloudinaryId: String = "",

        var createdAt: String = "",

        @Json(name="isMainImage")
        var isMainImage: Boolean = false
) : Parcelable