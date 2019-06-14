package com.square.android.data.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Reward(
        var imageUrl: String = "",

        var name: String = ""

) : Parcelable