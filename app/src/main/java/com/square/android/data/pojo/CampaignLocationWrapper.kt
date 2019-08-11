package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CampaignLocationWrapper(
        @Json(name="interval")
        val intervalId: Long = 0,
        var location: CampaignInterval.Location? = null
): Parcelable