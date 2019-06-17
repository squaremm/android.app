package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class CampaignLocationWrapper(
        @field:JsonProperty("interval")
        val intervalId: String = "",
        var location: CampaignInterval.Location? = null
)