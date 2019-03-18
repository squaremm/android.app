package com.square.android.data.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class RedemptionInfo(
        @field:JsonProperty("_id")
        var id: Long = 0,
        var closed: Boolean = false,
        var date: String = "",
        var endTime: String = "",
        var offers: List<Long> = listOf(),
        var place: PlaceInfo = PlaceInfo(),
        var claimed: Boolean = false,
        var startTime: String = "",
        var user: Int = 0
) {
}