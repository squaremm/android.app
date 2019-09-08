package com.square.android.data.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
class CampaignInfo(
        @Json(name="_id")
        var id: Long = 0,
        var title: String? = null,
        var type: String? = null,
        var mainImage: String? = null,
        var winners: List<Campaign.Winner>? = null,
        var daysToStart: Int = 0,
        var daysToPicture: Int = 0,
        var daysToInstagramPicture: Int = 0,
        var hasWinner: Boolean = false
)