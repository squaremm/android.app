package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class CampaignInfo(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var title: String? = null,
        var type: String? = null,
        var mainImage: String? = null,
        var daysToStart: Int = 0,
        var daysToPicture: Int = 0,
        var daysToInstagramPicture: Int = 0,
        var hasWinner: Boolean = false
)