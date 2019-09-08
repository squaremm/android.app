package com.square.android.data.pojo

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class CampaignBooking(
        var title: String? = null,
        var campaignId: Long = 0,
        var pickUpDate: String? = null,
        var mainImage: String? = null,


        //Used only in app
        var time: String? = null
): Parcelable