package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Day(
        var dayName: String = "",
        var dayValue: Int = 0,
        var monthNumber: Int = 0
) : Parcelable