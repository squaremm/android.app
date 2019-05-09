package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Images(
        var message: String = "",
        var images: List<Photo>? = null
) : Parcelable