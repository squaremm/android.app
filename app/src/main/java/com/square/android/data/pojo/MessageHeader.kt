package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MessageHeader(
        var date: String = "",
        var time: String = ""
) : Parcelable