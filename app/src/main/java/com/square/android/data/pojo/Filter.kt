package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(
        var text: String = "",
        var activated: Boolean = false
) : Parcelable