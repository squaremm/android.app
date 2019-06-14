package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageAspect(
        var imageUrl: String = "",
        var deleteImage: Boolean = false,
        var aspectType: Int = 0,
        var arrangeType: Int = 0
) : Parcelable