package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class Message(
        var id: String = "",
        var text: String = "",
        var userId: Long = 0,
        var image: String? = null,
        var timestamp: Long = 0,
        var sent: Boolean = true
) : Parcelable