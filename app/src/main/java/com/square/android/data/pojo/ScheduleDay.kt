package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleDay(
        var start: String = "",
        var end: String = ""
) : Parcelable