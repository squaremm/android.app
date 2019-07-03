package com.square.android.data.pojo

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class CampaignBooking(
        var title: String? = null,
        var campaignId: Long = 0,
        var pickUpDate: String? = null,
        var mainImage: String? = null
): Parcelable