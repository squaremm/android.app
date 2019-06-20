package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CampaignLocationWrapper(
        @field:JsonProperty("interval")
        val intervalId: Long = 0,
        var location: CampaignInterval.Location? = null
): Parcelable