package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
        var id: String = "",

        var url: String = "",

        var cloudinaryId: String = "",

        var createdAt: String = "",

        @field:JsonProperty("isMainImage")
        var isMainImage: Boolean = false
) : Parcelable